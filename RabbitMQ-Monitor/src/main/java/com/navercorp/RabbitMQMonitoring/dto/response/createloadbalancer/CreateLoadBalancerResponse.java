package com.navercorp.RabbitMQMonitoring.dto.response.createloadbalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLoadBalancerResponse {

    @JsonProperty(value = "returnCode")
    private String returnCode;

    @JsonProperty(value = "returnMessage")
    private String returnMessage;

    @JsonProperty(value = "totalRows")
    private String totalRows;

    @JsonProperty(value = "loadBalancerInstanceList")
    private LoadBalancerInstance[] loadBalancerInstanceList;

}
