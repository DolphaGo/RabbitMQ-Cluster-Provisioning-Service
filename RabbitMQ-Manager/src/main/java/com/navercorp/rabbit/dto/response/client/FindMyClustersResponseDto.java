package com.navercorp.rabbit.dto.response.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
public class FindMyClustersResponseDto {
    List<String> clusterNameList=new ArrayList<>();

    public FindMyClustersResponseDto(List<String> clusterNameList){
        this.clusterNameList=clusterNameList;
    }
}
