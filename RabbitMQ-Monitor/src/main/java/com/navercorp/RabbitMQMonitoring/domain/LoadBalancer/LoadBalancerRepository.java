package com.navercorp.RabbitMQMonitoring.domain.LoadBalancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface LoadBalancerRepository extends JpaRepository<LoadBalancer,Long> {

    @Query("select l from LoadBalancer l where l.loadBalancerInstanceNo=:loadBalancerInstanceNo")
    LoadBalancer findByLoadBalancerInstanceNo(@Param("loadBalancerInstanceNo") String loadBalancerInstanceNo);

    @Query("select l from LoadBalancer l where l.cluster.clusterName =:clusterName")
    LoadBalancer findByClusterName(@Param("clusterName") String clusterName);

    @Transactional @Modifying
    @Query("update LoadBalancer l set l.loadBalancerInstanceStatusCode=:loadBalancerStatusCode , l.loadBalancerInstanceStatusCodeName =:loadBalancerStatusCodeName where l.loadbalancerId =:loadbalancerId")
    void setLoadBalancerState(@Param("loadbalancerId") Long loadbalancerId,@Param("loadBalancerStatusCode") String loadBalancerStatusCode,@Param("loadBalancerStatusCodeName") String loadBalancerStatusCodeName);
}
