package com.navercorp.rabbit.dto.response.scaling;

import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ScaleOutResponse {
    ServerInstancesResponse response;
    Node newNode;
    Node targetNode;
    List<Node> nodes;

    @Builder
    public ScaleOutResponse(ServerInstancesResponse response,Node newNode, Node targetNode,List<Node> nodes) {
        this.response=response;
        this.newNode = newNode;
        this.targetNode = targetNode;
        this.nodes=nodes;
    }
}
