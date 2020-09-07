package com.navercorp.rabbit.dto.request.scale;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ScaleInRequestDto {
    private String clusterName;

    @Builder
    public ScaleInRequestDto(String clusterName){
        this.clusterName=clusterName;
    }
}
