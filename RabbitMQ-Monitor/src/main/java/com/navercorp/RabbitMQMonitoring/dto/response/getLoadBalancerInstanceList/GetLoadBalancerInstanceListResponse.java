package com.navercorp.RabbitMQMonitoring.dto.response.getLoadBalancerInstanceList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLoadBalancerInstanceListResponse {
    @JsonProperty(value = "returnCode")
    private String returnCode;

    @JsonProperty(value = "returnMessage")
    private String returnMessage;

    @JsonProperty(value = "totalRows")
    private String totalRows;

    @JsonProperty(value = "loadBalancerInstanceList")
    private LoadBalancerInstance[] loadBalancerInstanceList;
}
