package com.navercorp.RabbitMQMonitoring.service;

import com.navercorp.RabbitMQMonitoring.domain.Cluster.Cluster;
import com.navercorp.RabbitMQMonitoring.domain.LoadBalancer.LoadBalancer;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.domain.Port.Port;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ResourceService {
    private final DBService db;
    private final LoadBalancerService loadBalancerService;

    public void ResourceUpdate(){
        clusterDBUpdate();
        portDBUpdate();
    }

    //유령 클러스터 제거 로직
    private void clusterDBUpdate(){
        List<Cluster> clusters=db.findAllClusters();
        for(Cluster cluster:clusters){
            List<Node> nodes=db.findNodesByClusterName(cluster.getClusterName());
            if(nodes.size()==0) {
                if(cluster.isOnLoadBalancer()){
                    LoadBalancer loadBalancer=db.findLoadBalancerByClusterName(cluster.getClusterName());
                    if(loadBalancer!=null) {
                        log.info(cluster.getClusterName() + "에 옵션으로 있던 로드밸런서 " + loadBalancer.getLoadBalancerInstanceNo() + "를 삭제합니다.");
                        db.deleteLoadBalancer(loadBalancer);
                        loadBalancerService.deleteLoadBalancer(loadBalancer);
                    }else{
                        db.setLoadBalancerStateOfCluster(cluster.getClusterId(),false);
                    }
                }
                log.info(cluster.getClusterName()+"을 삭제합니다.");
                db.deleteCluster(cluster);
            }
        }
    }

    //유령 포트 제거 로직
    private void portDBUpdate(){
        List<Port> ports=db.getPorts(true);

        List<Node> nodes=db.findAllNodes();
        HashMap<String,Boolean> portMap=new HashMap<>();
        for(Node node:nodes) {
            String port=node.getPortForwardingPort();
            if(port!=null)  portMap.put(port,true);
        }

        for(Port port:ports){
            String portNum=port.getPortNum();
            if(portMap.get(portNum)==null) {
                log.info("유령포트 :"+portNum+"를 사용 해제 처리합니다.");
                db.setStatePort(port.getPortId(),false);
            }
        }
    }

}
