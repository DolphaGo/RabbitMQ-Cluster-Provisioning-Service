package com.navercorp.rabbit.domain.Cluster;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ClusterRepository extends JpaRepository<Cluster, Long> {

    @Query("select c from Cluster c where c.clusterName=:clusterName")
    Cluster findClusterByClusterName(@Param("clusterName") String clusterName);


    @Transactional @Modifying
    @Query("update Cluster c set c.onLoadBalancer=:onLoadBalancer where c.clusterId=:clusterId")
    void setOnLoadBalancer(@Param("clusterId") Long clusterId, @Param("onLoadBalancer") boolean onLoadBalancer);


    @Modifying
    @Transactional
    @Query("update Cluster c set c.cpuUtilization=:cpuUtilization where c.clusterId=:clusterId")
    void updateCPUUtilization(@Param("clusterId") Long clusterId,@Param("cpuUtilization") double cpuUtilization);

    @Modifying
    @Transactional
    @Query("update Cluster c set c.autoScale=:state where c.clusterId =:clusterId")
    void setAutoScaleState(@Param("clusterId") Long clusterId,@Param("state") boolean state);
}
