package com.navercorp.rabbit.service;

import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.LoadBalancer.LoadBalancer;
import com.navercorp.rabbit.dto.response.deleteloadbalancer.DeleteLoadBalancerResponse;
import com.navercorp.rabbit.exception.cluster.ClusterNotExistException;
import com.navercorp.rabbit.exception.loadbalancer.CanNotCreateLoadBalancerException;
import com.navercorp.rabbit.exception.loadbalancer.CanNotDeleteLoadBalancerException;
import com.navercorp.rabbit.dto.response.deleteloadbalancer.DeleteLoadBalancerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoadBalancerService {
    private final UtilService utilService;
    private final SchedulerService schedulerService;
    private final DBService db;

    @Transactional
    public void createLoadBalancer(String clusterName) {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();
        if(cluster.isOnLoadBalancer()) throw new CanNotCreateLoadBalancerException();

        schedulerService.createLoadBalancerScheduling(clusterName);
    }

    public DeleteLoadBalancerResponse deleteLoadBalancer(String clusterName){
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        //로직 시작
        if(cluster.isOnLoadBalancer()) {
            db.setLoadBalancerState(cluster.getClusterId(),false);
        }

        if(cluster.getLoadbalancer()==null) {
            throw new CanNotDeleteLoadBalancerException();
        }

        log.info(cluster.getClusterName()+"에 달려있는 "+cluster.getLoadbalancer().getLoadBalancerName()+"를 삭제합니다.");
        LoadBalancer loadBalancer=cluster.getLoadbalancer();

        UriComponentsBuilder signitureURI=utilService.makeSignitureURI("loadbalancer","deleteLoadBalancerInstances",loadBalancer.getLoadBalancerInstanceNo() );
        DeleteLoadBalancerResponseDto responseDto=utilService.deleteLoadBalancerRequest(signitureURI.toUriString());
        DeleteLoadBalancerResponse response=responseDto.getDeleteLoadBalancerResponse();
        //db 처리
        db.deleteLoadBalancerRequest(loadBalancer);

        log.info("deleteLoadBalancer------->"+responseDto.getDeleteLoadBalancerResponse().getReturnMessage());

        return response;
    }

}
