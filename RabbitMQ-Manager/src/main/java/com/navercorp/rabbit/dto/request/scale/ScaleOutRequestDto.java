package com.navercorp.rabbit.dto.request.scale;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScaleOutRequestDto {
    private String clusterName;

    @Builder
    public ScaleOutRequestDto(String clusterName){
        this.clusterName=clusterName;
    }
}
