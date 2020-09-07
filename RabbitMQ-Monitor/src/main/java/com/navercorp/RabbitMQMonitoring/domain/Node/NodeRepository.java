package com.navercorp.RabbitMQMonitoring.domain.Node;

import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NodeRepository extends JpaRepository<Node, Long> {
    @Query("select n from Node n where n.serverInstanceNo=:nodeInstanceNo")
    Node findByInstanceNo(@Param("nodeInstanceNo") String nodeInstanceNo);

    @Transactional
    @Modifying
    @Query("update Node n set n.portForwardingPort=:portNum where n.nodeId=:nodeId")
    void setForwardingPort(@Param("nodeId") Long nodeId, @Param("portNum") String portNum);

    @Transactional
    @Modifying
    @Query("update Node n set n.password=:password where n.nodeId=:nodeId")
    void setPassword(@Param("nodeId") Long nodeId, @Param("password") String password);

    @Transactional
    @Modifying
    @Query("update Node n set n.portForwardingConfigurationNo=:portForwardingConfigurationNo where n.nodeId=:nodeId")
    void setPortForwardingConfigurationNo(@Param("nodeId") Long nodeId, @Param("portForwardingConfigurationNo") String portForwardingConfigurationNo);

    @Transactional
    @Modifying
    @Query("update Node n set n.serverStatusCode=:serverInstanceStatusCode, n.serverStatusCodeName=:serverInstanceStatusCodeName where n.nodeId=:nodeId")
    void setServerStatus(@Param("nodeId") Long nodeId,
                         @Param("serverInstanceStatusCode") String serverInstanceStatusCode,
                         @Param("serverInstanceStatusCodeName") String serverInstanceStatusCodeName);

    @Transactional
    @Modifying
    @Query("update Node n set n.serverStatusCode=:serverInstanceStatusCode, n.serverStatusCodeName=:serverInstanceStatusCodeName where n.serverInstanceNo=:serverInstanceNo")
    void setServerStatusByServerInstanceNo(
            @Param("serverInstanceNo") String serverInstanceNo,
            @Param("serverInstanceStatusCode") String serverInstanceStatusCode,
            @Param("serverInstanceStatusCodeName") String serverInstanceStatusCodeName
    );


    @Query("select n from Node n where n.cluster.clusterName=:clusterName")
    List<Node> findNodesByClusterName(@Param("clusterName") String clusterName);

}
