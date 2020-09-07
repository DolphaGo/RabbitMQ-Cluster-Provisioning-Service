package com.navercorp.RabbitMQMonitoring.dto.response.deleteloadbalancer;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeleteLoadBalancerResponseDto {
    @JsonProperty(value = "deleteLoadBalancerInstancesResponse")
    private DeleteLoadBalancerResponse deleteLoadBalancerResponse;
}
