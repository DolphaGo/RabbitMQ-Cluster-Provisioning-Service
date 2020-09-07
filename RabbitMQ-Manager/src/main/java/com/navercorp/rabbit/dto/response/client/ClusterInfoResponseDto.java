package com.navercorp.rabbit.dto.response.client;

import com.navercorp.rabbit.domain.Cluster.Cluster;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ClusterInfoResponseDto {
    String clusterName;
    String loadBalancerIp;
    String loadBalancerPort;
    List<NodeListResponseDto> NodeListResponseDtos;
    boolean OnloadBalancer;
    boolean autoScale;

    @Builder
    public ClusterInfoResponseDto(String clusterName, String loadBalancerIp, String loadBalancerPort, List<NodeListResponseDto> nodeListResponseDtos, boolean onloadBalancer, boolean autoScale) {
        this.clusterName = clusterName;
        this.loadBalancerIp = loadBalancerIp;
        this.loadBalancerPort = loadBalancerPort;
        NodeListResponseDtos = nodeListResponseDtos;
        OnloadBalancer = onloadBalancer;
        this.autoScale = autoScale;
    }
}
