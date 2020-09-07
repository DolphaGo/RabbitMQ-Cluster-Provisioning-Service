package com.navercorp.RabbitMQMonitoring.dto.response.createCluster;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstancesResponse;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateClusterResponseDto {

    @JsonProperty(value = "createServerInstancesResponse")
    private ServerInstancesResponse serverInstancesResponse;

}
