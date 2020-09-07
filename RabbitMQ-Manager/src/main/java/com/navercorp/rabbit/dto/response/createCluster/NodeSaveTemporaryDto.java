package com.navercorp.rabbit.dto.response.createCluster;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NodeSaveTemporaryDto {
    String serverInstanceNo;
    String serverName;
    String privateIp;
    String portForwardingPublicIp;
    String serverInstanceStatusCode;
    String serverInstanceStatusCodeName;
    String portForwardingPort;

    @Builder
    public NodeSaveTemporaryDto(String serverInstanceNo, String serverName, String privateIp, String portForwardingPublicIp, String serverInstanceStatusCode,String serverInstanceStatusCodeName,String portForwardingPort) {
        this.serverInstanceNo = serverInstanceNo;
        this.serverName = serverName;
        this.privateIp = privateIp;
        this.portForwardingPublicIp = portForwardingPublicIp;
        this.serverInstanceStatusCode = serverInstanceStatusCode;
        this.serverInstanceStatusCodeName=serverInstanceStatusCodeName;
        this.portForwardingPort=portForwardingPort;
    }

}
