package com.navercorp.rabbit.dto.response.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NodeListResponseDto {
    String serverInstanceNo;
    String serverName;
    String privateIp;
    String userId;
    String password;
    String serverStatusCode;
    String portForwardingPublicIp;
    String portForwardingPort;

    @Builder
    public NodeListResponseDto(String serverInstanceNo, String serverName, String privateIp, String userId, String password, String serverStatusCode, String portForwardingPublicIp, String portForwardingPort) {
        this.serverInstanceNo = serverInstanceNo;
        this.serverName = serverName;
        this.privateIp = privateIp;
        this.userId = userId;
        this.password = password;
        this.serverStatusCode = serverStatusCode;
        this.portForwardingPublicIp = portForwardingPublicIp;
        this.portForwardingPort = portForwardingPort;
    }
}
