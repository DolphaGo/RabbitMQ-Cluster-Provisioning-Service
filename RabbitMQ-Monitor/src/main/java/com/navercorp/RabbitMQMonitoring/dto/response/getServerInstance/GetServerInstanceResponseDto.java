package com.navercorp.RabbitMQMonitoring.dto.response.getServerInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstancesResponse;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetServerInstanceResponseDto {
    @JsonProperty(value = "getServerInstanceListResponse")
    private ServerInstancesResponse getServerInstanceResponse;
}
