package com.navercorp.rabbit.service;

import com.navercorp.rabbit.config.ncloud.NcloudConfig;
import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.rabbit.dto.response.createCluster.NodeSaveTemporaryDto;
import com.navercorp.rabbit.dto.response.getRootPassword.PasswordResponse;
import com.navercorp.rabbit.dto.response.portForwarding.PortConfigNoResponse;
import com.navercorp.rabbit.dto.response.portForwarding.PortConfigNoResponseDto;
import com.navercorp.rabbit.dto.response.scaling.ScaleOutResponse;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstance;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import com.navercorp.rabbit.exception.cluster.ClusterNotExistException;
import com.navercorp.rabbit.exception.scale.ScaleInException;
import com.navercorp.rabbit.exception.scale.ScaleOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ScaleService {
    private final NcloudConfig properties;
    private final JSchService jschService;
    private final UtilService utilService;
    private final DBService db;

    @Transactional
    public void leaveCluster(Node deleteNode) throws Exception{
        jschService.leaveCluster(deleteNode);
    }

    @Transactional
    public ScaleOutResponse scaleOut(String clusterName) throws Exception {

        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        List<Node> nodes=db.findNodesByClusterName(clusterName);
        if(nodes.size()>=5) throw new ScaleOutException();

        //새로운 노드 생성
        List<Port> list = db.getPorts(false);
        Port port=list.get(0);
        db.setStatePort(port.getPortId(),true);
        String nameNumber=port.getPortNum();

        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "createServerInstances", 1, nameNumber);
        CreateClusterResponseDto responseDto = utilService.createClusterRequest(signitureURI.toUriString());

        // make node for DB Save
        ServerInstancesResponse response = responseDto.getServerInstancesResponse();

        ServerInstance[] serverInstanceList = response.getServerInstanceList();

        ServerInstance serverInstance = serverInstanceList[0];
        String serverInstanceNo = serverInstance.getServerInstanceNo();
        String serverName = serverInstance.getServerName();
        String privateIp = serverInstance.getPrivateIp();
        String portForwardingPublicIp = serverInstance.getPortForwardingPublicIp();
        String serverInstanceStatusCode = serverInstance.getServerInstanceStatus().getCode();
        String serverInstanceStatusCodeName= serverInstance.getServerInstanceStatus().getCodeName();
        String portForwardingPort=port.getPortNum();

        //return 값 중 필요 정보 Get
        NodeSaveTemporaryDto temporaryDto = NodeSaveTemporaryDto.builder()
                .serverInstanceNo(serverInstanceNo)
                .serverName(serverName)
                .privateIp(privateIp)
                .portForwardingPublicIp(portForwardingPublicIp)
                .serverInstanceStatusCode(serverInstanceStatusCode)
                .serverInstanceStatusCodeName(serverInstanceStatusCodeName)
                .portForwardingPort(portForwardingPort)
                .build();

        Node targetNode=nodes.get(0);
        Node node=db.saveNodeForScaleOut(temporaryDto,cluster);
        return new ScaleOutResponse(response,node,targetNode,nodes);
    }


    @Transactional
    public boolean getPassword(Node node) {
        if(node.getPassword()!=null) return true;

        String serverInstanceNo = node.getServerInstanceNo();

        try {
            String privateKey = URLEncoder.encode(properties.getNS_secret(), String.valueOf(StandardCharsets.UTF_8));
            URIBuilder builder = new URIBuilder();
            String Uri = builder.setScheme(null).setHost(null).setPath("/server/v2/getRootPassword")
                    .setParameter("responseFormatType", "json")
                    .setParameter("serverInstanceNo", serverInstanceNo)
                    .build().toString();

            Uri = Uri + "&privateKey=" + privateKey;

            PasswordResponse response = utilService.getPasswordRequest(Uri).getPasswordResponse();
            String password = response.getRootPassword();
            log.info("password={}", password);

            db.setPassword(node.getNodeId(), password);
            node.setPassword(password);

            return true;
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * PortforwardConfigurationNo 가져오기
     */
    @Transactional
    public boolean getPortConfigNo(Node node) {

        log.info("start getPortConfigNo........");
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getPortForwardingRuleList");
        PortConfigNoResponseDto responseDto = utilService.getPortConfigNoRequest(signitureURI.toUriString());
        PortConfigNoResponse response = responseDto.getPortConfigNoResponse();
        String portForwardingConfigurationNo = response.getPortForwardingConfigurationNo();
        db.setPortForwardingConfigurationNo(node.getNodeId(), portForwardingConfigurationNo);
        node.setPortForwardingConfigurationNo(portForwardingConfigurationNo);

        log.info("finish getPortConfigNo........");
        return allocatePorts(node, portForwardingConfigurationNo);
    }

    /**
     * 사용가능한 포트 조회, 포트 할당 및 node 저장
     */
    @Transactional
    public boolean allocatePorts(Node node, String portForwardingConfigurationNo) {
        log.info("start allocatePorts........");

        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "addPortForwardingRules", portForwardingConfigurationNo, node, node.getPortForwardingPort());

        boolean flag = utilService.allocatePortsRequest(signitureURI.toUriString());

        // 할당 요청 성공시, DB Update
        if (flag) {
            log.info("finish allocatePorts........");
            return true;

        } else return false;
    }

    public Node selectNodeForScaleIn(String clusterName) {
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster==null) throw new ClusterNotExistException();

        List<Node> nodes=cluster.getNodes();
        int size=nodes.size();
        if(size<=2) throw new ScaleInException();

        Node deleteNode=nodes.get(size-1);
        return deleteNode;
    }
}
