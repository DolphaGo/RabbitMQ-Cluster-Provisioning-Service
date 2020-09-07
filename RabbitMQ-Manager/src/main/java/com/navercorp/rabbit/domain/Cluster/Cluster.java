package com.navercorp.rabbit.domain.Cluster;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.navercorp.rabbit.domain.BaseTimeEntity;
import com.navercorp.rabbit.domain.LoadBalancer.LoadBalancer;
import com.navercorp.rabbit.domain.Node.Node;
import com.sun.istack.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Cluster extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clusterId;

    private String clusterName;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Node> nodes = new ArrayList<>();

    @OneToOne(mappedBy = "cluster", fetch = FetchType.LAZY)
    @JsonBackReference
    private LoadBalancer loadbalancer;

    private boolean onLoadBalancer;

    @Column(nullable = true)
    private double cpuUtilization;

    @Column(nullable = true)
    private boolean autoScale;

    @Builder
    public Cluster(String clusterName,boolean onLoadBalancer) {
        this.clusterName = clusterName;
        this.onLoadBalancer=onLoadBalancer;
    }

    public void setLoadBalancer(LoadBalancer loadBalancer) {
        this.loadbalancer=loadBalancer;
    }

    public void setOnLoadBalancer(boolean onLoadBalancer){
        this.onLoadBalancer=onLoadBalancer;
    }

    public void setAutoScale(boolean autoScale){ this.autoScale=autoScale;}
}
