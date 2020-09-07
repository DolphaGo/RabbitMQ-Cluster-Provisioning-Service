package com.navercorp.rabbit.service;

import com.navercorp.rabbit.domain.LoadBalancer.LoadBalancer;
import com.navercorp.rabbit.dto.response.client.ClusterInfoResponseDto;
import com.navercorp.rabbit.dto.response.client.NodeListResponseDto;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstance;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import com.navercorp.rabbit.exception.cluster.ClusterNotExistException;
import com.navercorp.rabbit.exception.cluster.CreateClusterRequestException;
import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.createCluster.NodeSaveTemporaryDto;
import com.navercorp.rabbit.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.rabbit.dto.response.stopCluster.StopClusterResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClusterService {
    private final UtilService utilService;
    private final SchedulerService schedulerService;
    private final LoadBalancerService loadBalancerService;
    private final DBService db;

    /**
     * Cluster를 생성하고, 필요한 정보를 DB화 하는 서비스 로직
     */
    @Transactional
    public CreateClusterResponseDto createClusterRequest(int nodeCount) throws Exception{
        Port[] ports = new Port[nodeCount];
        List<Port> list = db.getPorts(false);
        for(int i=0;i<nodeCount;i++) {
            ports[i]=list.get(i);
            db.setStatePort(ports[i].getPortId(),true);
        }

        String nameNumber=ports[nodeCount-1].getPortNum();
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "createServerInstances", nodeCount, nameNumber);
        CreateClusterResponseDto responseDto = utilService.createClusterRequest(signitureURI.toUriString());

        // make nodes for DB Save
        ServerInstancesResponse response = responseDto.getServerInstancesResponse();

        int totalRows = response.getTotalRows();

        if(nodeCount!=totalRows) throw new CreateClusterRequestException();

        ServerInstance[] serverInstanceList = response.getServerInstanceList();
        List<NodeSaveTemporaryDto> temporaryNodes = new ArrayList<>();

        for (int i = 0; i < totalRows; i++) {
            ServerInstance serverInstance = serverInstanceList[i];
            String serverInstanceNo = serverInstance.getServerInstanceNo();
            String serverName = serverInstance.getServerName();
            String privateIp = serverInstance.getPrivateIp();
            String portForwardingPublicIp = serverInstance.getPortForwardingPublicIp();
            String serverInstanceStatusCode = serverInstance.getServerInstanceStatus().getCode();
            String serverInstanceStatusCodeName= serverInstance.getServerInstanceStatus().getCodeName();
            String portForwardingPort=ports[i].getPortNum();

            //return 값 중 필요 정보 Get
            NodeSaveTemporaryDto temporaryNode = NodeSaveTemporaryDto.builder()
                    .serverInstanceNo(serverInstanceNo)
                    .serverName(serverName)
                    .privateIp(privateIp)
                    .portForwardingPublicIp(portForwardingPublicIp)
                    .serverInstanceStatusCode(serverInstanceStatusCode)
                    .serverInstanceStatusCodeName(serverInstanceStatusCodeName)
                    .portForwardingPort(portForwardingPort)
                    .build();

            temporaryNodes.add(temporaryNode);
        }

        //cluster DB Save;
        List<Node> nodes = db.clusterSave(temporaryNodes,nameNumber);

        //Scheduling : running 상태 확인 -> 비밀번호, port 작업 -> clustering
        schedulerService.startClusteringScheduling(nodes,ports);


        return responseDto;
    }


    @Transactional
    public StopClusterResponseDto stopClusterRequest(String clusterName) throws InterruptedException {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        List<Node> nodes = db.findNodesByClusterName(clusterName);
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "stopServerInstances", nodes);
        StopClusterResponseDto responseDto = utilService.stopClusterRequest(signitureURI.toUriString());

        //cluster내의 노드들이 모두 멈출 때까지 Scheduling
        schedulerService.stopClusterScheduling(nodes);

        return responseDto;
    }

    @Transactional
    public void terminateClusterRequest(String clusterName) {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        //Loadbalancer를 option으로 달고 있던 Cluster라면 Loadbalancer 반납.
        if(cluster.isOnLoadBalancer()){
            loadBalancerService.deleteLoadBalancer(clusterName);
        }

        List<Node> nodes=db.findNodesByClusterName(clusterName);
        //cluster내의 노드들이 모두 반환될 때 까지 Scheduling
        schedulerService.terminateClusterScheduling(nodes);
    }

    public List<Cluster> findAllClusters() {
        return db.findAllClusters();
    }

    public void setAutoScaleState(String clusterName, boolean state) {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        db.setAutoScaleState(cluster.getClusterId(),state);
    }

    public ClusterInfoResponseDto detailInfoOfCluster(String clusterName) {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        List<Node> nodes=cluster.getNodes();

        boolean autoScale=cluster.isAutoScale();
        boolean isOnLoadBalancer=cluster.isOnLoadBalancer();
        String loadBalancerIp=null, loadBalancerPort=null;

        if(isOnLoadBalancer){
            LoadBalancer loadBalancer=cluster.getLoadbalancer();
            loadBalancerIp =loadBalancer.getVirtualIp();
            loadBalancerPort=loadBalancer.getLoadBalancerPort();
        }

        List<NodeListResponseDto> nodeListResponseDtos=new ArrayList<>();
        for(Node node:nodes){
            NodeListResponseDto nodeListResponseDto=NodeListResponseDto.builder()
                    .serverName(node.getServerName())
                    .serverInstanceNo(node.getServerInstanceNo())
                    .privateIp(node.getPrivateIp())
                    .portForwardingPublicIp(node.getPortForwardingPublicIp())
                    .portForwardingPort(node.getPortForwardingPort())
                    .userId("root")
                    .password(node.getPassword())
                    .serverStatusCode(node.getServerStatusCode())
                    .build();
            nodeListResponseDtos.add(nodeListResponseDto);
        }

        ClusterInfoResponseDto clusterInfoResponseDto=ClusterInfoResponseDto.builder()
                .clusterName(clusterName)
                .onloadBalancer(isOnLoadBalancer)
                .autoScale(autoScale)
                .nodeListResponseDtos(nodeListResponseDtos)
                .loadBalancerIp(loadBalancerIp)
                .loadBalancerPort(loadBalancerPort)
                .build();
        return clusterInfoResponseDto;
    }
}
