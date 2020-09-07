package com.navercorp.RabbitMQMonitoring.service;

import com.navercorp.RabbitMQMonitoring.domain.Cluster.Cluster;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.domain.Port.Port;
import com.navercorp.RabbitMQMonitoring.dto.response.getServerInstance.GetServerInstanceResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.scaling.ScaleOutResponse;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstance;
import com.navercorp.RabbitMQMonitoring.dto.response.stopCluster.StopClusterResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.terminateCluster.TerminateClusterResponseDto;
import com.navercorp.RabbitMQMonitoring.util.scheduler.TaskScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final ScaleService scaleService;
    private final TaskScheduler scheduler;
    private final UtilService utilService;
    private final JSchService jschService;
    private final LoadBalancerService loadBalancerService;
    private final DBService db;

    private boolean stopNodeTask(String serverInstanceNo) {
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "stopServerInstances", serverInstanceNo);
        StopClusterResponseDto responseDto=utilService.stopClusterRequest(signitureURI.toUriString());
        String returnMessage=responseDto.getServerInstancesResponse().getReturnMessage();
        log.info("stopNodeTask returnMessage={}",returnMessage);
        if(returnMessage.equals("success")) return true;
        else return false;
    }

    public void terminateNodeScheduling(Node node){
        String scheduleId="terminateNodeTask-"+node.getServerInstanceNo();
        Runnable runnable = () -> terminateNodeTask(node,scheduleId);
        scheduler.register(runnable, scheduleId, 10000);
    }

    private void terminateNodeTask(Node node,String scheduleId) {
        log.info("terminateNodeTask......:"+scheduleId);
        ServerInstance[] serverInstanceList = getInstanceState(node.getServerInstanceNo());
        boolean flag = checkInstanceState(serverInstanceList, "NSTOP");
        if (flag) { // 서버가 중지되었다면
            if(returnResource(node)) {
                scheduler.remove(scheduleId);
            }
        }else stopNodeTask(node.getServerInstanceNo());
    }

    private boolean returnResource(Node node) {
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "terminateServerInstances", node.getServerInstanceNo());
        TerminateClusterResponseDto responseDto = utilService.terminateClusterRequest(signitureURI.toUriString());
        String portNum = node.getPortForwardingPort();
        db.setStatePortByPortNum(portNum, false);
        db.deleteNode(node);
        log.info("returnResource Response :"+responseDto.getServerInstancesResponse().getReturnMessage());
        return true;
    }

    public void terminateUnSavedNodeScheduling(String serverInstanceNo){
        String scheduleId="terminateUnSavedNodeTask-"+serverInstanceNo;
        Runnable runnable = () -> terminateUnSavedNodeTask(serverInstanceNo,scheduleId);
        scheduler.register(runnable, scheduleId, 15000);
    }

    private void terminateUnSavedNodeTask(String serverInstanceNo,String scheduleId) {
        ServerInstance[] serverInstanceList = getInstanceState(serverInstanceNo);
        boolean flag = checkInstanceState(serverInstanceList, "NSTOP");
        if (flag) { // 서버가 중지되었다면
            String returnMessage=returnUnSavedNodeResource(serverInstanceNo);
            log.info("returnMessage:"+returnMessage);
            if(returnMessage.equals("success")) scheduler.remove(scheduleId);
        }else stopNodeTask(serverInstanceNo);
    }

    public String returnUnSavedNodeResource(String serverInstanceNo) {
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "terminateServerInstances", serverInstanceNo);
        TerminateClusterResponseDto responseDto = utilService.terminateClusterRequest(signitureURI.toUriString());
        return responseDto.getServerInstancesResponse().getReturnMessage();
    }

    public ServerInstance[] getInstanceState(String serverInstanceNo){
        UriComponentsBuilder signitureURI = utilService.makeSignitureURI("server", "getServerInstanceList", serverInstanceNo);
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList(signitureURI.toUriString());
        ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
        return serverInstanceList;
    }

    /**
     * 현재 관심 서버 대상 status check
     */
    public boolean checkInstanceState(ServerInstance[] serverInstanceList, String status) {
        log.info("start checkServer------>"+status);

        boolean flag=true;
        for (ServerInstance serverInstance : serverInstanceList) {
            String serverInstanceStatusCode = serverInstance.getServerInstanceStatus().getCode();
            if (!serverInstanceStatusCode.equals(status)) {
                flag=false;
            }
        }
        if(flag) log.info("모든 서버가"+ status+"가 되었습니다........");
        return flag;
    }

    public void scaleOutScheduling(Node newNode, Node targetNode, List<Node> nodes, Cluster cluster) throws Exception{
        String scheduleId="scaleOutTask-"+newNode.getServerInstanceNo();
        log.info("scaleOutScheduling 시작합니다....:"+scheduleId);
        Runnable runnable = () -> {
            try {
                scaleOutTask(newNode,targetNode,nodes,cluster,scheduleId);
            } catch (Exception e) {
                log.error("scaleOutScheduling Error:"+e.getMessage());
                scheduler.remove(scheduleId);
            }
        };
        scheduler.register(runnable, scheduleId, 30000);
    }

    public void scaleOutTask(Node newNode,Node targetNode,List<Node> nodes,Cluster cluster,String scheduleId) throws Exception{
        log.info("scaleOutTask..............");
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
                    if(cluster.isOnLoadBalancer()){
                        String loadBalancerInstanceNo=cluster.getLoadbalancer().getLoadBalancerInstanceNo();
                        log.info("loadBalancerInstanceNo={}",loadBalancerInstanceNo);
                        loadBalancerService.changeLoadBalancedServerInstances(loadBalancerInstanceNo,newNode,nodes);
                    }
                    scheduler.remove(scheduleId);
                    return;
                }
            }
        }
    }

    public boolean checkCanStoppable(String serverInstanceNo) {
        ServerInstance[] serverInstanceList = getInstanceState(serverInstanceNo);
        return checkInstanceState(serverInstanceList, "RUN");
    }

    public void loadBalancerRecoveryScheduling(Cluster cluster, String loadBalancerInstanceNo,String serverInstanceNo) throws Exception {
        String scheduleId="loadBalancerRecoveryTask-"+loadBalancerInstanceNo;
        log.info("loadBalancerRecoveryScheduling 시작합니다....:"+scheduleId);
        Runnable runnable = () -> {
            try {
                loadBalancerRecoveryTask(cluster,serverInstanceNo,scheduleId);
            } catch (Exception e) {
                log.error("loadBalancerRecoveryScheduling Error:"+e.getMessage());
                scheduler.remove(scheduleId);
            }
        };
        scheduler.register(runnable, scheduleId, 60000); //1분
    }

    private void loadBalancerRecoveryTask(Cluster cluster, String serverInstanceNo, String scheduleId) throws Exception {
        Node deleteNode=db.findNodeByNodeInstanceNo(serverInstanceNo);
        ServerInstance[] serverInstanceList = getInstanceState(deleteNode.getServerInstanceNo());
        boolean isStopped = checkInstanceState(serverInstanceList, "NSTOP");

        log.info(deleteNode.getServerName()+"를 삭제할 예정입니다.");
        //scale in
        if (isStopped) {
            if(returnResource(deleteNode)) {
                scheduler.remove(scheduleId);

                //ScaleOut
                ScaleOutResponse scaleOutResponse = scaleService.scaleOut(cluster);
                Node newNode = scaleOutResponse.getNewNode();
                Node targetNode = scaleOutResponse.getTargetNode();
                List<Node> nodesForScaleOut = scaleOutResponse.getNodes();
                scaleOutSchedulingForLoadBalancerRecovery(newNode,targetNode,nodesForScaleOut,cluster,scheduleId);
            }
        }else {
            scaleService.leaveCluster(deleteNode); //클러스터 이격
            stopNodeTask(deleteNode.getServerInstanceNo());
        }
    }

    public void scaleOutSchedulingForLoadBalancerRecovery(Node newNode, Node targetNode, List<Node> nodes, Cluster cluster,String scheduleId) throws Exception{
        log.info("scaleOutSchedulingForLoadBalancerRecovery 시작합니다....:"+scheduleId);
        Runnable runnable = () -> {
            try {
                scaleOutTask(newNode,targetNode,nodes,cluster,scheduleId);
            } catch (Exception e) {
                log.error("scaleOutScheduling Error:"+e.getMessage());
                scheduler.remove(scheduleId);
            }
        };
        scheduler.register(runnable, scheduleId, 60000);
    }

    public boolean isScheduling(String TaskName, String loadBalancerInstanceNo) {
        return scheduler.isScheduling(TaskName+"-"+loadBalancerInstanceNo);
    }
}

