package com.navercorp.rabbit.dto.response.LoadBalancerInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerHealthCheckStatus {

    @JsonProperty(value = "protocolType")
    private CommonCode protocolType;

    @JsonProperty(value = "loadBalancerPort")
    private Integer loadBalancerPort;

    @JsonProperty(value = "serverPort")
    private Integer serverPort;

    @JsonProperty(value = "l7HealthCheckPath")
    private String l7HealthCheckPath;

    @JsonProperty(value = "proxyProtocolUseYn")
    private String proxyProtocolUseYn;

    @JsonProperty(value = "serverStatus")
    private Boolean serverStatus;
}
