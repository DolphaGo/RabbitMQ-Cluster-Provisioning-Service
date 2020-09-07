package com.navercorp.rabbit.domain.LoadBalancer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoadBalancerRepository extends JpaRepository<LoadBalancer,Long> {

    @Query("select l from LoadBalancer l where l.loadBalancerInstanceNo=:loadBalancerInstanceNo")
    LoadBalancer findByLoadBalancerInstanceNo(@Param("loadBalancerInstanceNo") String loadBalancerInstanceNo);
}
