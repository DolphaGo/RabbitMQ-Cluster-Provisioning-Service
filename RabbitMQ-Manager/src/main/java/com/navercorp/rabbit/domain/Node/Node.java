package com.navercorp.rabbit.domain.Node;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.navercorp.rabbit.domain.BaseTimeEntity;
import com.navercorp.rabbit.domain.Cluster.Cluster;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class Node extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long nodeId;

    @Column
    private String serverInstanceNo;

    @Column
    private String serverName;

    @Column
    private String privateIp;

    @Column
    private String portForwardingPublicIp;

    @Column(nullable = true)
    private String portForwardingPort;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true)
    private String portForwardingConfigurationNo;

    @Column(nullable = true)
    private String serverStatusCode;

    @Column(nullable = true)
    private String serverStatusCodeName;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Cluster cluster;

    @Builder
    public Node(String serverInstanceNo, String serverName, String privateIp, Cluster cluster, String portForwardingPublicIp, String portForwardingPort, String password, String portForwardingConfigurationNo, String serverStatusCode,String serverStatusCodeName) {
        this.serverInstanceNo = serverInstanceNo;
        this.serverName = serverName;
        this.privateIp = privateIp;
        this.cluster = cluster;
        this.portForwardingPublicIp = portForwardingPublicIp;
        this.portForwardingPort = portForwardingPort;
        this.password = password;
        this.portForwardingConfigurationNo = portForwardingConfigurationNo;
        this.serverStatusCode = serverStatusCode;
        this.serverStatusCodeName=serverStatusCodeName;
        cluster.getNodes().add(this);
    }

    public void setPortForwardingPort(String portForwardingPort) {
        this.portForwardingPort = portForwardingPort;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPortForwardingConfigurationNo(String portForwardingConfigurationNo) {
        this.portForwardingConfigurationNo = portForwardingConfigurationNo;
    }

    public void setServerStatus(String serverStatusCode,String serverStatusCodeName) {
        this.serverStatusCode = serverStatusCode;
        this.serverStatusCodeName = serverStatusCodeName;
    }
}
