package com.navercorp.rabbit.service;

import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.client.ClusterInfoResponseDto;
import com.navercorp.rabbit.dto.response.client.FindMyClustersResponseDto;
import com.navercorp.rabbit.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.rabbit.dto.response.deleteloadbalancer.DeleteLoadBalancerResponse;
import com.navercorp.rabbit.dto.response.scaling.ScaleOutResponse;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import com.navercorp.rabbit.dto.response.stopCluster.StopClusterResponseDto;
import com.navercorp.rabbit.exception.common.CanNotAllowRequestException;
import com.navercorp.rabbit.exception.scale.ScaleInRunningException;
import com.navercorp.rabbit.exception.scale.ScaleOutRunningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ClientService {
    private final ClusterService clusterService;
    private final LoadBalancerService loadBalancerService;
    private final ScaleService scaleService;
    private final SchedulerService schedulerService;

    public CreateClusterResponseDto createClusterRequest(int nodeCount) throws Exception {
        CreateClusterResponseDto response = clusterService.createClusterRequest(nodeCount);
        return response;
    }

    public void terminateClusterRequest(String clusterName) {
        clusterService.terminateClusterRequest(clusterName);
    }

    public void createLoadBalancer(String clusterName) {
        loadBalancerService.createLoadBalancer(clusterName);
    }

    public DeleteLoadBalancerResponse deleteLoadBalancer(String clusterName) {
        return loadBalancerService.deleteLoadBalancer(clusterName);
    }

    public Node scaleIn(String clusterName) throws Exception {
        //현재 같은 클러스터에 대해 scaling 스케줄이 진행되고 있다면 Exception 처리
        if(schedulerService.checkScheduling("scaleOutTask-"+clusterName)) throw new ScaleOutRunningException();
        if(schedulerService.checkScheduling("scaleInTask-"+clusterName)) throw new ScaleInRunningException();

        Node deleteNode=scaleService.selectNodeForScaleIn(clusterName);

        //클러스터 이격
        scaleService.leaveCluster(deleteNode);
        //서버 반납 요청
        schedulerService.terminateNodeScheduling(deleteNode,clusterName);

        return deleteNode;
    }

    public ServerInstancesResponse scaleOut(String clusterName) throws Exception {
        //현재 같은 클러스터에 대해 scaling 스케줄이 진행되고 있다면 Exception 처리
        if(schedulerService.checkScheduling("scaleInTask-"+clusterName)) throw new ScaleInRunningException();
        if(schedulerService.checkScheduling("scaleOutTask-"+clusterName)) throw new ScaleOutRunningException();

        //새로운 노드 생성 & DB 저장
        ScaleOutResponse scaleOutResponse=scaleService.scaleOut(clusterName);

        //Scheduling : running 상태 확인 -> 비밀번호, port 작업 -> clustering
        ServerInstancesResponse response=scaleOutResponse.getResponse();
        Node newNode=scaleOutResponse.getNewNode();
        Node targetNode=scaleOutResponse.getTargetNode();
        List<Node> nodes=scaleOutResponse.getNodes();

        schedulerService.scaleOutScheduling(newNode,targetNode,nodes,clusterName);

        return response;
    }

    public FindMyClustersResponseDto findAllClusters() {
        List<Cluster> list= clusterService.findAllClusters();
        List<String> names=new ArrayList<>();
        for(Cluster cluster:list){
            names.add(cluster.getClusterName());
        }
        FindMyClustersResponseDto findMyClustersResponseDto=new FindMyClustersResponseDto(names);
        return findMyClustersResponseDto;
    }


    public void onAutoScale(String clusterName) {
        clusterService.setAutoScaleState(clusterName,true);
    }

    public void offAutoScale(String clusterName) {
        clusterService.setAutoScaleState(clusterName,false);
    }

    public ClusterInfoResponseDto detailInfoOfCluster(String clusterName) {
        return clusterService.detailInfoOfCluster(clusterName);
    }
}
