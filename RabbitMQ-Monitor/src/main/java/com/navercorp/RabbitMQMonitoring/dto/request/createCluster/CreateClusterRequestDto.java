package com.navercorp.RabbitMQMonitoring.dto.request.createCluster;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateClusterRequestDto {
    int nodeCount;

    @Builder
    public CreateClusterRequestDto(int nodeCount) {
        this.nodeCount = nodeCount;
    }
}
