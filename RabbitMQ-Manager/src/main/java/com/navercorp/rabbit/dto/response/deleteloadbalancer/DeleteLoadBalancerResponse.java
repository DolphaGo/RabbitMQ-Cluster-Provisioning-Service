package com.navercorp.rabbit.dto.response.deleteloadbalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.rabbit.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteLoadBalancerResponse {
    @JsonProperty(value = "returnCode")
    private String returnCode;

    @JsonProperty(value = "returnMessage")
    private String returnMessage;

    @JsonProperty(value = "totalRows")
    private String totalRows;

    @JsonProperty(value = "loadBalancerInstanceList")
    private LoadBalancerInstance[] loadBalancerInstanceList;
}
