package com.navercorp.rabbit.service;

import com.navercorp.rabbit.config.ncloud.NcloudConfig;
import com.navercorp.rabbit.dto.response.changeLoadBalancedServerInstances.ChangeLoadBalancedServerInstancesResponseDto;
import com.navercorp.rabbit.dto.response.createloadbalancer.CreateLoadBalancerResponse;
import com.navercorp.rabbit.dto.response.createloadbalancer.CreateLoadBalancerResponseDto;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstance;
import com.navercorp.rabbit.exception.loadbalancer.CanNotCreateLoadBalancerException;
import com.navercorp.rabbit.util.scheduler.TaskScheduler;
import com.navercorp.rabbit.domain.Cluster.Cluster;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.getRootPassword.PasswordResponse;
import com.navercorp.rabbit.dto.response.getServerInstance.GetServerInstanceResponseDto;
import com.navercorp.rabbit.dto.response.portForwarding.PortConfigNoResponse;
import com.navercorp.rabbit.dto.response.portForwarding.PortConfigNoResponseDto;
import com.navercorp.rabbit.dto.response.terminateCluster.TerminateClusterResponseDto;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final NcloudConfig properties;
    private final TaskScheduler scheduler;

    private final ScaleService scaleService;
    private final JSchService jschService;
    private final UtilService utilService;
    private final DBService db;

    @Transactional
    public void startClusteringScheduling(List<Node> nodes, Port[] ports) {
        String scheduleId="checkServerRunningTask-"+nodes.get(0).getServerInstanceNo();
        Runnable runnable = () -> {
            try {
                clusteringTask(nodes,ports,scheduleId);
            } catch (Exception e) {
                log.error("startClusteringScheduling",e);

            }
        };
        scheduler.register(runnable, scheduleId, 30000);
    }

    //scheduling...
    public void clusteringTask(List<Node> nodes, Port[] ports, String scheduleId) throws Exception{
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getServerInstanceList", nodes);
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList(signitureURI.toUriString());
        ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
        boolean isServerRunning = checkInstanceState(serverInstanceList, "RUN");
        //모든 서버가 켜졌다면
        if (isServerRunning) {
            if (getPassword(nodes)) {
                if (getPortConfigNo(nodes)) {
                    clustering(nodes);
                    scheduler.remove(scheduleId);
                    return;
                }
            }
        }
    }

    /**
     * 현재 관심 서버 대상 status check
     */
    public boolean checkInstanceState(ServerInstance[] serverInstanceList, String status) {
        log.info("Check InstanceState ------> "+status);

        boolean flag=true;
        for (ServerInstance serverInstance : serverInstanceList) {
            String serverInstanceStatusCode = serverInstance.getServerInstanceStatus().getCode();
            String serverInstanceStatusCodeName=serverInstance.getServerInstanceStatus().getCodeName();
            String serverInstanceNo = serverInstance.getServerInstanceNo();

            //상태 DB 업데이트
            db.setServerStatusByServerInstanceNo(serverInstanceNo, serverInstanceStatusCode,serverInstanceStatusCodeName);

            if (!serverInstanceStatusCode.equals(status)) {
                flag=false;
            }
        }
        if(flag) log.info("모든 서버가 "+ status+"상태 입니다........");
        return flag;
    }

    /**
     * 생성한 인스턴스의 root 비밀번호 가져오기
     */
    @Transactional
    public boolean getPassword(List<Node> nodes) {
        int cnt=0;
        for(Node node:nodes){
            if(node.getPassword()==null) {
                cnt++;
            }
        }
        if(cnt==0) return true;

        log.info("start getPassword........" + cnt + "개를 처리해야 합니다.");
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if(node.getPassword()!=null) continue;

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

            } catch (URISyntaxException | UnsupportedEncodingException e) {
                log.error(e.getMessage());
                return false;
            }
        }

        log.info("finish getPassword........");
        return true;
    }

    /**
     * PortforwardConfigurationNo 가져오기
     */
    @Transactional
    public boolean getPortConfigNo(List<Node> nodes) {
        log.info("start getPortConfigNo........");
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getPortForwardingRuleList");
        PortConfigNoResponseDto responseDto = utilService.getPortConfigNoRequest(signitureURI.toUriString());
        PortConfigNoResponse response = responseDto.getPortConfigNoResponse();
        String portForwardingConfigurationNo = response.getPortForwardingConfigurationNo();
        for (int j = 0; j < nodes.size(); j++) {
            Node node = nodes.get(j);
            db.setPortForwardingConfigurationNo(node.getNodeId(), portForwardingConfigurationNo);
            node.setPortForwardingConfigurationNo(portForwardingConfigurationNo);
        }

        log.info("finish getPortConfigNo........");
        boolean flag = allocatePorts(nodes, portForwardingConfigurationNo);

        return flag;
    }


    /**
     * 사용가능한 포트 조회, 포트 할당 및 node 저장
     */
    @Transactional
    public boolean allocatePorts(List<Node> nodes, String portForwardingConfigurationNo) {
        log.info("start allocatePorts........");

        UriComponentsBuilder signitureURI = utilService.makeAllocatePortsSignitureURI("server", "addPortForwardingRules", portForwardingConfigurationNo, nodes);

        boolean flag = utilService.allocatePortsRequest(signitureURI.toUriString());

        if (flag) {
            log.info("finish allocatePorts........");
            return true;

        } else return false;
    }


    /**
     * 클러스터링하기
     */
    @Transactional
    public void clustering(List<Node> nodes) {
        log.info("clustering start...........");
        try {
            jschService.initCluster(nodes);
        } catch (Exception e) {
            log.error("initClustering 중 에러가 발생했습니다 :" + e.getMessage());
        }

        try {
            jschService.joinCluster(nodes);
        } catch (Exception e) {
            log.error("JoinCluster 중 에러가 발생했습니다 :" + e.getMessage());
        }

        try {
            jschService.mirroring(nodes.get(0));
        } catch (Exception e) {
            log.error("mirroring 중 에러가 발생했습니다 :" + e.getMessage());
        }

        log.info("clustering finish...........");
    }

    public ServerInstance[] getInstanceState(Node node){
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getServerInstanceList", node);
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList(signitureURI.toUriString());
        ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
        return serverInstanceList;
    }

    public ServerInstance[] getInstanceState(List<Node> nodes){
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getServerInstanceList", nodes);
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList(signitureURI.toUriString());
        ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
        return serverInstanceList;
    }


    public void stopClusterScheduling(List<Node> nodes) {
        String scheduleId="stopClusterTask-"+nodes.get(0).getServerInstanceNo();
        Runnable runnable = () -> {
            try {
                stopClusterTask(nodes,scheduleId);
            } catch (InterruptedException e) {
                log.error("stopClusterScheduling Error:"+e.getMessage());
                scheduler.remove(scheduleId);
            }
        };
        scheduler.register(runnable, scheduleId, 10000);
    }

    public void stopClusterTask(List<Node> nodes,String scheduleId) throws InterruptedException {
        ServerInstance[] serverInstanceList = getInstanceState(nodes);
        boolean flag = checkInstanceState(serverInstanceList, "NSTOP");
        //모든 서버가 중지되었다면
        if (flag) {
            scheduler.remove(scheduleId);
        }else{
            if(checkServerRunning(nodes)) {
                UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "stopServerInstances", nodes);
                utilService.stopClusterRequest(signitureURI.toUriString());
            }
        }
    }

    public void terminateNodeScheduling(Node node, String clusterName) {
        String scheduleId="scaleInTask-"+clusterName;
        Runnable runnable = () -> {
            try {
                terminateNodeTask(node,scheduleId);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        };
        scheduler.register(runnable, scheduleId, 10000);
    }

    private void terminateNodeTask(Node node,String scheduleId) throws InterruptedException {
        ServerInstance[] serverInstanceList = getInstanceState(node);
        boolean flag = checkInstanceState(serverInstanceList, "NSTOP");
        if (flag) { // 서버가 중지되었다면
            Node deleteNode=db.findNodeForSync(node);
            if(deleteNode!=null) returnNodeResource(deleteNode);
            scheduler.remove(scheduleId);
        }else {
            if(checkServerRunning(node)) {
                UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "stopServerInstances", node);
                utilService.stopClusterRequest(signitureURI.toUriString());
            }
        }
    }

    public boolean returnNodeResource(Node node) throws InterruptedException {
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "terminateServerInstances", node);
        TerminateClusterResponseDto responseDto = utilService.terminateClusterRequest(signitureURI.toUriString());
        String portNum = node.getPortForwardingPort();
        db.setStatebyPortNum(portNum, false);
        db.deleteNode(node);
        return true;
    }

    public void terminateClusterScheduling(List<Node> nodes) {
        String scheduleId="terminateClusterTask-"+nodes.get(0).getServerInstanceNo();
        Runnable runnable = () -> {
            try {
                terminateClusterTask(nodes,scheduleId);
            } catch (InterruptedException e) {
                log.error("terminateClusterScheduling Error:"+e.getMessage());
                scheduler.remove(scheduleId);
            }
        };
        scheduler.register(runnable, scheduleId, 10000);
    }

    public void terminateClusterTask(List<Node> nodes,String scheduleId) throws InterruptedException {
        log.info(scheduleId+"가 terminateClusterTask를 실행합니다...");

        ServerInstance[] serverInstanceList = getInstanceState(nodes);
        boolean flag = checkInstanceState(serverInstanceList, "NSTOP");
        if (flag) { //모든 서버가 중지되었다면
            ArrayList<Node> deleteNodes=db.findNodesForSync(nodes);
            if(deleteNodes.size()>0) returnClusterResource(deleteNodes);
            scheduler.remove(scheduleId);
        }else {
            if(checkServerRunning(nodes)) {
                UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "stopServerInstances", nodes);
                utilService.stopClusterRequest(signitureURI.toUriString());
            }
        };
    }

    @Transactional
    public void returnClusterResource(List<Node> nodes) {
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "terminateServerInstances", nodes);
        TerminateClusterResponseDto responseDto = utilService.terminateClusterRequest(signitureURI.toUriString());

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            String portNum = node.getPortForwardingPort();
            db.setStatebyPortNum(portNum, false);
        }
        //클러스터와 노드는 1:N Cascade_ALL로 묶어놨기에 cluster만 삭제해도 모두 삭제됨.
        db.deleteCluster(nodes.get(0).getCluster());
    }

    public void  scaleOutScheduling(Node newNode,Node targetNode,List<Node> nodes,String clusterName) throws Exception{
        String scheduleId="scaleOutTask-"+clusterName;
        Runnable runnable = () -> {
            try {
                scaleOutTask(newNode,targetNode,scheduleId,nodes,clusterName);
            } catch (Exception e) {
                log.error("scaleOutScheduling Error:"+e.getMessage());
            }
        };
        scheduler.register(runnable, scheduleId, 30000);
    }

    public void scaleOutTask(Node newNode,Node targetNode,String scheduleId,List<Node> nodes,String clusterName) throws Exception{
        log.info("scaleOutTask....:"+scheduleId+"가 시작합니다...");
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getServerInstanceList", newNode);
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList(signitureURI.toUriString());
        ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
        boolean isServerRunning = checkInstanceState(serverInstanceList, "RUN");
        //모든 서버가 켜졌다면
        if (isServerRunning) {
            if (scaleService.getPassword(newNode)) {
                if (scaleService.getPortConfigNo(newNode)) {
                    jschService.joinCluster(newNode,targetNode,nodes);
                    //로드 밸런서를 가지고 있는 클러스터에 대해선 로드밸런서 업데이트가 필요함.
                    Cluster cluster=db.findClusterByClusterName(clusterName);
                    if(cluster.isOnLoadBalancer()){
                        String loadBalancerInstanceNo=cluster.getLoadbalancer().getLoadBalancerInstanceNo();
                        log.info("loadBalancerInstanceNo={}",loadBalancerInstanceNo);
                        changeLoadBalancedServerInstances(loadBalancerInstanceNo,newNode,nodes);
                    }
                    scheduler.remove(scheduleId);
                    return;
                }
            }
        }
    }

    public ChangeLoadBalancedServerInstancesResponseDto changeLoadBalancedServerInstances(String loadBalancerInstanceNo,Node newNode,List<Node> nodes) {
        UriComponentsBuilder signitureURI=utilService.makeSignitureURI("loadbalancer","changeLoadBalancedServerInstances",loadBalancerInstanceNo,newNode, nodes);
        ChangeLoadBalancedServerInstancesResponseDto responseDto=utilService.changeLoadBalancedServerInstancesRequest(signitureURI.toUriString());
        return responseDto;
    }

    public boolean checkServerRunning(List<Node> nodes) {
        ServerInstance[] serverInstanceList = getInstanceState(nodes);
        boolean flag = checkInstanceState(serverInstanceList, "RUN");
        return flag;
    }

    public boolean checkServerRunning(Node node) {
        ServerInstance[] serverInstanceList = getInstanceState(node);
        boolean flag = checkInstanceState(serverInstanceList, "RUN");
        return flag;
    }

    public void createLoadBalancerScheduling(String clusterName) {
        String scheduleId="createLoadBalancerScheduling-"+clusterName;
        Runnable runnable = () -> {
            createLoadBalancerTask(clusterName,scheduleId);
        };
        scheduler.register(runnable, scheduleId, 10000);
    }

    public void createLoadBalancerTask(String clusterName,String scheduleId){
        Cluster cluster=db.findClusterByClusterName(clusterName);
        if(cluster.isOnLoadBalancer()) {
            scheduler.remove(scheduleId);
            throw new CanNotCreateLoadBalancerException();
        }

        //요청 및 생성
        List<Node> nodes=db.findNodesByClusterName(clusterName);
        boolean isServerRunning= checkServerRunning(nodes);

        if(!isServerRunning) return;

        String networkUsageTypeCode="PRVT";
        String protocolTypeCode="TCP";
        String loadBalancerPort="5672";
        String serverPort="5672";

        UriComponentsBuilder signitureURI=utilService.makeSignitureURI(
                "loadbalancer",
                "createLoadBalancerInstance",
                networkUsageTypeCode,
                nodes,
                protocolTypeCode,
                loadBalancerPort,
                serverPort);
        CreateLoadBalancerResponseDto responseDto=utilService.createLoadBalancerRequest(signitureURI.toUriString());

        //저장
        CreateLoadBalancerResponse createLoadBalancerResponse=responseDto.getCreateLoadBalancerResponse();
        db.saveLoadBalancerRequest(cluster,createLoadBalancerResponse);

        log.info("createLoadBalancer----------->"+responseDto.getCreateLoadBalancerResponse().getReturnMessage());
        scheduler.remove(scheduleId);
    }

    public boolean checkScheduling(String scheduleId) {
        return scheduler.isScheduling(scheduleId);
    }
}

