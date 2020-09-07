package com.navercorp.RabbitMQMonitoring.dto.response.changeLoadBalancedServerInstances;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeLoadBalancedServerInstancesResponseDto {

    @JsonProperty(value = "changeLoadBalancedServerInstancesResponse")
    private LoadBalancerInstance loadBalancerInstance;

}
