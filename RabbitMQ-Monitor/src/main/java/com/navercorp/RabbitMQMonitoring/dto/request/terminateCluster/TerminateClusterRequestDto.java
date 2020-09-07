package com.navercorp.RabbitMQMonitoring.dto.request.terminateCluster;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TerminateClusterRequestDto {
    String clusterName;

    @Builder
    public TerminateClusterRequestDto(String clusterName) {
        this.clusterName = clusterName;
    }
}
