package com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstance;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadBalancedServerInstance {

    @JsonProperty(value = "serverInstance")
    private ServerInstance serverInstance;

    @JsonProperty(value = "serverHealthCheckStatusList")
    private List<ServerHealthCheckStatus> serverHealthCheckStatusList;

}
