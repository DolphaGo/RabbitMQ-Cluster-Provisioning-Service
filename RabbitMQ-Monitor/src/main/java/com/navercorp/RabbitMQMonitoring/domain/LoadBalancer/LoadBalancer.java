package com.navercorp.RabbitMQMonitoring.domain.LoadBalancer;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.navercorp.RabbitMQMonitoring.domain.Cluster.Cluster;
import com.navercorp.RabbitMQMonitoring.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Entity
public class LoadBalancer extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loadbalancerId;

    private String loadBalancerInstanceNo;

    private String loadBalancerName;

    private String virtualIp;

    private String loadBalancerPort;

    private String loadBalancerInstanceStatusCode;

    private String loadBalancerInstanceStatusCodeName;

    @OneToOne
    @JsonBackReference
    @Fetch(value = FetchMode.SELECT)
    private Cluster cluster;

    @Builder
    public LoadBalancer(String loadBalancerInstanceNo, String virtualIp, String loadBalancerName, String loadBalancerInstanceStatusCode, String loadBalancerInstanceStatusCodeName, String loadBalancerPort,Cluster cluster) {
        this.loadBalancerInstanceNo = loadBalancerInstanceNo;
        this.virtualIp = virtualIp;
        this.loadBalancerName = loadBalancerName;
        this.loadBalancerInstanceStatusCode = loadBalancerInstanceStatusCode;
        this.loadBalancerInstanceStatusCodeName=loadBalancerInstanceStatusCodeName;
        this.loadBalancerPort=loadBalancerPort;
        this.cluster=cluster;
        this.cluster.setLoadBalancer(this);
    }

}
