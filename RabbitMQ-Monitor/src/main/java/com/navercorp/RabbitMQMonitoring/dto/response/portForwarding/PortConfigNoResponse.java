package com.navercorp.RabbitMQMonitoring.dto.response.portForwarding;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortConfigNoResponse {

    @JsonProperty(value = "returnMessage")
    private String returnMessage;
    @JsonProperty(value = "portForwardingConfigurationNo")
    private String portForwardingConfigurationNo;
}
