package com.navercorp.RabbitMQMonitoring.dto.request.createLoadBalancer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateLoadBalancerRequestDto {
    String clusterName;

    @Builder
    public CreateLoadBalancerRequestDto(String clusterName){
        this.clusterName=clusterName;
    }
}
