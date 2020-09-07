package com.navercorp.rabbit.dto.request.deleteLoadBalancer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeleteLoadBalancerRequestDto {
    String clusterName;

    @Builder
    public DeleteLoadBalancerRequestDto(String clusterName){
        this.clusterName=clusterName;
    }
}
