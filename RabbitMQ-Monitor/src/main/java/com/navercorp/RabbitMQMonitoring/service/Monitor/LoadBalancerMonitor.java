package com.navercorp.RabbitMQMonitoring.service.Monitor;

import com.navercorp.RabbitMQMonitoring.domain.Cluster.Cluster;
import com.navercorp.RabbitMQMonitoring.domain.LoadBalancer.LoadBalancer;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.CommonCode;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.LoadBalancedServerInstance;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.LoadBalancerInstance;
import com.navercorp.RabbitMQMonitoring.dto.response.LoadBalancerInstance.ServerHealthCheckStatus;
import com.navercorp.RabbitMQMonitoring.dto.response.getLoadBalancerInstanceList.GetLoadBalancerInstanceListResponse;
import com.navercorp.RabbitMQMonitoring.dto.response.getLoadBalancerInstanceList.GetLoadBalancerInstanceListResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstance;
import com.navercorp.RabbitMQMonitoring.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class LoadBalancerMonitor {
    private final DBService db;
    private final UtilService utilService;
    private final ScaleService scaleService;
    private final SchedulerService schedulerService;
    private final ResourceService ResourceService;

    //클러스터 단위로 LoadBalancer 상태를 모니터링 한다.
    @Scheduled(fixedRate = 30000, initialDelay = 5000)
    public void getLoadBalancerInstanceList() throws Exception {
        ResourceService.ResourceUpdate();

        log.info("getLoadBalancerInstanceList.......");

        List<Cluster> clusters = db.findAllClusters();

        for(Cluster cluster:clusters) {
            //로드밸런서 옵션을 달은 클러스터에 대해서만 진행한다.
            if(!cluster.isOnLoadBalancer()) {
                log.info(cluster.getClusterName()+"은 로드밸런서가 없습니다.");
                continue;
            }
            log.info(cluster.getClusterName()+"의 로드밸런서 상태를 조회합니다.");
            LoadBalancer loadBalancer=cluster.getLoadbalancer();
            String loadBalancerInstanceNo = loadBalancer.getLoadBalancerInstanceNo();

            GetLoadBalancerInstanceListResponseDto responseDto = utilService.getLoadBalancerInstanceList(loadBalancerInstanceNo);
            GetLoadBalancerInstanceListResponse response = responseDto.getGetLoadBalancerInstanceListResponse();

            LoadBalancerInstance[] loadBalancerInstances = response.getLoadBalancerInstanceList();
            LoadBalancerInstance loadBalancerInstance=loadBalancerInstances[0];
            String instanceNo = loadBalancerInstance.getLoadBalancerInstanceNo();
            List<LoadBalancedServerInstance> loadBalancedServerInstances = loadBalancerInstance.getLoadBalancedServerInstanceList();

            HashMap<String,Boolean> vmCheck=new HashMap<>();
            HashMap<String,Boolean> healthCheck=new HashMap<>();

            for (LoadBalancedServerInstance loadBalancedServerInstance : loadBalancedServerInstances) {
                ServerInstance serverInstance=loadBalancedServerInstance.getServerInstance();

                ServerHealthCheckStatus serverHealthCheckStatus = loadBalancedServerInstance.getServerHealthCheckStatusList().get(0);
                String serverInstanceNo=serverInstance.getServerInstanceNo();

                healthCheck.put(serverInstanceNo,serverHealthCheckStatus.getServerStatus());

                if(serverInstance.getServerInstanceStatus().getCode().equals("RUN")) vmCheck.put(serverInstanceNo,true);
                else vmCheck.put(serverInstanceNo,false);
            }
            CommonCode loadBalancerStatus = loadBalancerInstance.getLoadBalancerInstanceStatus();
            String loadBalancerStatusCode=loadBalancerStatus.getCode();
            String loadBalancerStatusCodeName=loadBalancerStatus.getCodeName();
            log.info(instanceNo+"의 loadBalancerStatus={}", loadBalancerStatusCode);
            //로드밸런서 상태 업데이트
            db.setLoadBalancerState(loadBalancer.getLoadbalancerId(),loadBalancerStatusCode,loadBalancerStatusCodeName);

            // 로드밸런서가 불안정한 상태일 때
            //if(!loadBalancerStatusCode.equals("USED")){
                //VM 상태 또는 health 상태가 좋지 않은 인스턴스에 대해 복구작업
                for(String serverInstanceNo:vmCheck.keySet()){
                    if(!vmCheck.get(serverInstanceNo) && !healthCheck.get(serverInstanceNo)){
                        if(!schedulerService.isScheduling("loadBalancerRecoveryTask",loadBalancerInstanceNo)) {
                            log.info(serverInstanceNo+"의 연결이 불안정합니다. 로드밸런서 연결을 다시 설정합니다.");
                            schedulerService.loadBalancerRecoveryScheduling(cluster, loadBalancerInstanceNo, serverInstanceNo);
                        }
                    }
                }
            //}
        }

    }
}
