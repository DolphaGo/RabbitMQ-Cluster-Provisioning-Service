package com.navercorp.RabbitMQMonitoring.service;

import com.navercorp.RabbitMQMonitoring.domain.LoadBalancer.LoadBalancer;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.dto.response.changeLoadBalancedServerInstances.ChangeLoadBalancedServerInstancesResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.deleteloadbalancer.DeleteLoadBalancerResponse;
import com.navercorp.RabbitMQMonitoring.dto.response.deleteloadbalancer.DeleteLoadBalancerResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoadBalancerService {
    private final UtilService utilService;

    public ChangeLoadBalancedServerInstancesResponseDto changeLoadBalancedServerInstances(String loadBalancerInstanceNo,Node newNode, List<Node> nodes) {
        UriComponentsBuilder signitureURI=utilService.makeSignitureURI("loadbalancer","changeLoadBalancedServerInstances",loadBalancerInstanceNo,newNode, nodes);
        ChangeLoadBalancedServerInstancesResponseDto responseDto=utilService.changeLoadBalancedServerInstancesRequest(signitureURI.toUriString());
        return responseDto;
    }

    public DeleteLoadBalancerResponse deleteLoadBalancer(LoadBalancer loadBalancer){
        UriComponentsBuilder signitureURI=utilService.makeSignitureURI("loadbalancer","deleteLoadBalancerInstances",loadBalancer.getLoadBalancerInstanceNo() );
        DeleteLoadBalancerResponseDto responseDto=utilService.deleteLoadBalancerRequest(signitureURI.toUriString());
        DeleteLoadBalancerResponse response=responseDto.getDeleteLoadBalancerResponse();
        log.info("deleteLoadBalancer------->"+responseDto.getDeleteLoadBalancerResponse().getReturnMessage());
        return response;
    }



}
