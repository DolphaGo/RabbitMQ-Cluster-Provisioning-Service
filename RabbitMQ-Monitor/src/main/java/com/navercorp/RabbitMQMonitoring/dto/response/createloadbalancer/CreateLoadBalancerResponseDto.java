package com.navercorp.RabbitMQMonitoring.dto.response.createloadbalancer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLoadBalancerResponseDto {

    @JsonProperty(value = "createLoadBalancerInstanceResponse")
    private CreateLoadBalancerResponse createLoadBalancerResponse;
}
