package com.navercorp.rabbit.dto.request.stopCluster;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StopClusterRequestDto {
    String clusterName;

    @Builder
    public StopClusterRequestDto(String clusterName) {
        this.clusterName = clusterName;
    }
}
