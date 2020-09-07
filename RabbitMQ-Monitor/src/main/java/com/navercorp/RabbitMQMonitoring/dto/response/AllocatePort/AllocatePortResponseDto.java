package com.navercorp.RabbitMQMonitoring.dto.response.AllocatePort;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstancesResponse;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllocatePortResponseDto {
    @JsonProperty(value = "addPortForwardingRulesResponse")
    ServerInstancesResponse allocatePortResponse;
}
