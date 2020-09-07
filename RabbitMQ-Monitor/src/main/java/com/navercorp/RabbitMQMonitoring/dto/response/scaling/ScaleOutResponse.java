package com.navercorp.RabbitMQMonitoring.dto.response.scaling;

import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.domain.Port.Port;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ScaleOutResponse {
    Node newNode;
    Node targetNode;
    List<Node> nodes;

    @Builder
    public ScaleOutResponse(Node newNode, Node targetNode, List<Node> nodes) {
        this.newNode = newNode;
        this.targetNode = targetNode;
        this.nodes=nodes;
    }
}
