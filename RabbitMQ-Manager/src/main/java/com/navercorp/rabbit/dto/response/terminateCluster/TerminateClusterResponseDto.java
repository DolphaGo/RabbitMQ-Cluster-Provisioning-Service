package com.navercorp.rabbit.dto.response.terminateCluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TerminateClusterResponseDto {
    @JsonProperty(value = "terminateServerInstancesResponse")
    private ServerInstancesResponse serverInstancesResponse;
}
