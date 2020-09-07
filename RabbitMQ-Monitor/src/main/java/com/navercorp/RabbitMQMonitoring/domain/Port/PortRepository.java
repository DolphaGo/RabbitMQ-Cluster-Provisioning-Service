package com.navercorp.RabbitMQMonitoring.domain.Port;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PortRepository extends JpaRepository<Port, Long> {

    @Query(value = "select p from Port p where p.isAllocated=:portState")
    List<Port> getPorts(@Param("portState") boolean portState);

    @Transactional
    @Modifying
    @Query("update Port p set p.isAllocated=:portState where p.portId=:portId")
    void setState(@Param("portId") Long portId, @Param("portState") boolean portState);


    @Transactional
    @Modifying
    @Query("update Port p set p.isAllocated=:portState where p.portNum=:portNum")
    void setStatebyPortNum(@Param("portNum") String portNum, @Param("portState") boolean portState);

}
