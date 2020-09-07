package com.navercorp.rabbit.service;

import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Cluster.ClusterRepository;
import com.navercorp.rabbit.domain.LoadBalancer.LoadBalancer;
import com.navercorp.rabbit.domain.LoadBalancer.LoadBalancerRepository;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Node.NodeRepository;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.domain.Port.PortRepository;
import com.navercorp.rabbit.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import com.navercorp.rabbit.dto.response.LoadBalancerInstance.LoadBalancerRule;
import com.navercorp.rabbit.dto.response.createCluster.NodeSaveTemporaryDto;
import com.navercorp.rabbit.dto.response.createloadbalancer.CreateLoadBalancerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBService {
    private final LoadBalancerRepository loadBalancerRepository;
    private final ClusterRepository clusterRepository;
    private final NodeRepository nodeRepository;
    private final PortRepository portRepository;
    /**
     * 인스턴스(노드) DB save
     */
    @Transactional
    public List<Node> clusterSave(List<NodeSaveTemporaryDto> temporaryNodes, String nameNumber) {
        log.info("start clusterSave................");
        List<Node> nodes = new ArrayList<>();
        /**
         * cluster 저장
         */
        String clusterName = "cluster-" + nameNumber;
        Cluster cluster = new Cluster(clusterName, false);
        clusterRepository.save(cluster);

        /**
         * nodes 저장
         */
        for (int i = 0; i < temporaryNodes.size(); i++) {
            NodeSaveTemporaryDto temporaryNode = temporaryNodes.get(i);
            String serverInstanceNo = temporaryNode.getServerInstanceNo();
            String serverName = temporaryNode.getServerName();
            String privateIp = temporaryNode.getPrivateIp();
            String portForwardingPublicIp = temporaryNode.getPortForwardingPublicIp();
            String serverInstanceStatusCode = temporaryNode.getServerInstanceStatusCode();
            String serverInstanceStatusCodeName=temporaryNode.getServerInstanceStatusCodeName();
            String portForwardingPort=temporaryNode.getPortForwardingPort();

            Node node = Node.builder()
                    .cluster(cluster)
                    .serverInstanceNo(serverInstanceNo)
                    .serverName(serverName)
                    .privateIp(privateIp)
                    .portForwardingPublicIp(portForwardingPublicIp)
                    .serverStatusCode(serverInstanceStatusCode)
                    .serverStatusCodeName(serverInstanceStatusCodeName)
                    .portForwardingPort(portForwardingPort)
                    .build();

            nodeRepository.save(node);
            nodes.add(node);
        }

        log.info("finish clusterSave................");
        return nodes;
    }

    public void deleteCluster(Cluster cluster) {
        clusterRepository.delete(cluster);
    }

    /**
     * clusterName에 속한 Node들을 return하는 method
     */
    @Transactional
    public List<Node> findNodesByClusterName(String clusterName) {
        List<Node> nodes = nodeRepository.findNodesByClusterName(clusterName);
        return nodes;
    }


    @Transactional
    public List<String> findAllnodeInstanceNo() {
        List<Node> list = nodeRepository.findAll();
        List<String> nodeInstanceNoList = new ArrayList<>();
        for (Node node : list) {
            nodeInstanceNoList.add(node.getServerInstanceNo());
        }
        return nodeInstanceNoList;
    }

    @Transactional
    public Node findNodeByNodeInstanceNo(String nodeInstanceNo) {
        return nodeRepository.findByInstanceNo(nodeInstanceNo);
    }

    @Transactional
    public List<Cluster> findAllClusters() {
        return clusterRepository.findAll();
    }

    @Transactional
    public List<Node> findAllNodes() {
        return nodeRepository.findAll();
    }

    @Transactional
    public void deleteAllNodes() {
        clusterRepository.deleteAll();
    }


    public void setPortForwardingConfigurationNo(Long nodeId, String portForwardingConfigurationNo) {
        nodeRepository.setPortForwardingConfigurationNo(nodeId, portForwardingConfigurationNo);
    }

    public void setServerStatusByServerInstanceNo(String serverInstanceNo, String serverInstanceStatusCode, String serverInstanceStatusCodeName) {
        nodeRepository.setServerStatusByServerInstanceNo(serverInstanceNo, serverInstanceStatusCode,serverInstanceStatusCodeName);
    }

    public void setPassword(Long nodeId, String password) {
        nodeRepository.setPassword(nodeId, password);
    }

    public void setStatebyPortNum(String portNum, boolean b) {
        portRepository.setStatebyPortNum(portNum, false);
    }

    public void setServerStatus(Long nodeId, String serverInstanceStatusCode, String serverInstanceStatusCodeName) {
        nodeRepository.setServerStatus(nodeId, serverInstanceStatusCode, serverInstanceStatusCodeName);
    }

    public Cluster findClusterByClusterName(String clusterName) {
        return clusterRepository.findClusterByClusterName(clusterName);
    }

    @Transactional
    public void initPorts() {
        portRepository.deleteAll();
        for (int i = 1027; i <= 2000; i++) {
            portRepository.save(Port.builder().portNum(i + "").isAllocated(false).build());
        }
    }

    public boolean checkPortInit(){
        List<Port> ports=portRepository.findAll();
        if(ports.isEmpty()) return false;
        else return true;
    }

    public List<Port> getPorts(boolean portState){
        if(!checkPortInit()) initPorts();
        return portRepository.getPorts(portState);
    }


    public void saveLoadBalancerRequest(Cluster cluster,CreateLoadBalancerResponse createLoadBalancerResponse) {

        LoadBalancerInstance[] loadBalancerInstances=createLoadBalancerResponse.getLoadBalancerInstanceList();

        for(LoadBalancerInstance loadBalancerInstance:loadBalancerInstances){
            String loadBalancerInstanceNo=loadBalancerInstance.getLoadBalancerInstanceNo();
            String loadBalancerName=loadBalancerInstance.getLoadBalancerName();
            String virtualIp=loadBalancerInstance.getVirtualIp();
            String loadBalancerInstanceStatusCode=loadBalancerInstance.getLoadBalancerInstanceStatus().getCode();
            String loadBalancerInstanceStatusCodeName=loadBalancerInstance.getLoadBalancerInstanceStatus().getCodeName();
            LoadBalancerRule loadBalancerRule=loadBalancerInstance.getLoadBalancerRuleList().get(0);
            String loadBalancerPort=loadBalancerRule.getLoadBalancerPort().toString();


            LoadBalancer loadBalancer=LoadBalancer.builder()
                    .loadBalancerInstanceNo(loadBalancerInstanceNo)
                    .loadBalancerName(loadBalancerName)
                    .virtualIp(virtualIp)
                    .loadBalancerInstanceStatusCode(loadBalancerInstanceStatusCode)
                    .loadBalancerInstanceStatusCodeName(loadBalancerInstanceStatusCodeName)
                    .loadBalancerPort(loadBalancerPort)
                    .cluster(cluster)
                    .build();

            loadBalancerRepository.save(loadBalancer);
            clusterRepository.setOnLoadBalancer(cluster.getClusterId(),true);
            cluster.setOnLoadBalancer(true);
        }
    }

    @Transactional
    public void deleteLoadBalancerRequest(LoadBalancer loadBalancer) {
        loadBalancerRepository.delete(loadBalancer);
    }

    @Transactional
    public void deleteNode(Node node) {
        log.info(node.getServerName()+"를 삭제합니다...");
        nodeRepository.delete(node);
    }
    
    public Node saveNodeForScaleOut(NodeSaveTemporaryDto temporaryNode, Cluster cluster) {
        String serverInstanceNo = temporaryNode.getServerInstanceNo();
        String serverName = temporaryNode.getServerName();
        String privateIp = temporaryNode.getPrivateIp();
        String portForwardingPublicIp = temporaryNode.getPortForwardingPublicIp();
        String serverInstanceStatusCode = temporaryNode.getServerInstanceStatusCode();
        String serverInstanceStatusCodeName=temporaryNode.getServerInstanceStatusCodeName();
        String portForwardingPort=temporaryNode.getPortForwardingPort();

        Node node = Node.builder()
                .cluster(cluster)
                .serverInstanceNo(serverInstanceNo)
                .serverName(serverName)
                .privateIp(privateIp)
                .portForwardingPublicIp(portForwardingPublicIp)
                .serverStatusCode(serverInstanceStatusCode)
                .serverStatusCodeName(serverInstanceStatusCodeName)
                .portForwardingPort(portForwardingPort)
                .build();

        nodeRepository.save(node);
        return node;
    }

    public void updateAllocatedPort(Node node, Port port) {
        nodeRepository.setForwardingPort(node.getNodeId(), port.getPortNum());
        node.setPortForwardingPort(port.getPortNum());
    }

    public void setAutoScaleState(Long clusterId, boolean state) {
        clusterRepository.setAutoScaleState(clusterId,state);
    }

    public void setLoadBalancerState(Long clusterId, boolean state) {
        log.info(clusterId+"의 로드밸런서 상태를 "+state+"로 변경합니다.");
        clusterRepository.setOnLoadBalancer(clusterId,state);
    }

    public void setStatePort(Long portId, boolean state) {
        portRepository.setState(portId, state);
    }

    public ArrayList<Node> findNodesForSync(List<Node> nodes) {
        ArrayList<Node> findNodes=new ArrayList<>();
        for(Node node:nodes){
            findNodes.add(nodeRepository.findByNodeId(node.getNodeId()));
        }
        return findNodes;
    }

    public Node findNodeForSync(Node node) {
        Node findNodes=nodeRepository.findByNodeId(node.getNodeId());
        return findNodes;
    }
}
