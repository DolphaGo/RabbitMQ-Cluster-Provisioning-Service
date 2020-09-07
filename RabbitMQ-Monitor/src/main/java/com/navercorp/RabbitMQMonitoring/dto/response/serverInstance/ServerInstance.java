package com.navercorp.RabbitMQMonitoring.dto.response.serverInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInstance {
    @JsonProperty(value = "serverInstanceNo")
    private String serverInstanceNo;

    @JsonProperty(value = "serverName")
    private String serverName;

    @JsonProperty(value = "serverInstanceStatus")
    private ServerInstanceStatus serverInstanceStatus;

    @JsonProperty(value = "publicIp")
    private String publicIp;

    @JsonProperty(value = "privateIp")
    private String privateIp;

    @JsonProperty(value = "portForwardingPublicIp")
    private String portForwardingPublicIp;

}
