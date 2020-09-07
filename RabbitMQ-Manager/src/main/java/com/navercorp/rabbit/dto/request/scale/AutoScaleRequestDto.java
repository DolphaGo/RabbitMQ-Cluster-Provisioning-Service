package com.navercorp.rabbit.dto.request.scale;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AutoScaleRequestDto {
    String clusterName;

    @Builder
    public AutoScaleRequestDto(String clusterName){
        this.clusterName=clusterName;
    }
}
