package com.navercorp.RabbitMQMonitoring.dto.response.getLoadBalancerInstanceList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetLoadBalancerInstanceListResponseDto {

    @JsonProperty(value = "getLoadBalancerInstanceListResponse")
    GetLoadBalancerInstanceListResponse getLoadBalancerInstanceListResponse;
}
