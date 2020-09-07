package com.navercorp.RabbitMQMonitoring.service.Monitor;

import com.navercorp.RabbitMQMonitoring.domain.Cluster.Cluster;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList.MetricStatisticListResponse;
import com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList.MetricStatisticListResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList.MetricData;
import com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList.MetricStatistic;
import com.navercorp.RabbitMQMonitoring.dto.response.scaling.ScaleOutResponse;
import com.navercorp.RabbitMQMonitoring.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CPUMonitor {
    private final DBService db;
    private final UtilService utilService;
    private final ScaleService scaleService;
    private final SchedulerService schedulerService;
    private final ResourceService ResourceService;

    //클러스터 단위로 CPU 사용률을 모니터링 한다.
    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void getCPUUtilization() throws Exception {
        ResourceService.ResourceUpdate();
        log.info("getCPUUtilization.......");
        List<Cluster> clusters = db.findAllClusters();

        for (Cluster cluster : clusters) {
            List<Node> nodes=db.findNodesByClusterName(cluster.getClusterName());
            boolean isRunning=utilService.isRunningNodes(nodes);
            if(!isRunning) continue; //Run 상태에서만 조회.

            MetricStatisticListResponseDto responseDto = utilService.getMetricStatisticList(nodes);
            MetricStatisticListResponse response = responseDto.getMetricStatisticListResponse();
            String returnMessage = response.getReturnMessage();

            if(returnMessage.equals("success")) {
                double sumOfCPUUtilization = 0;


                List<MetricStatistic> metricStatistics = response.getMetricStatisticList();
                for (MetricStatistic metricStatistic : metricStatistics) {
                    String instanceNo = metricStatistic.getInstanceNo();
                    List<MetricData> metricDataList = metricStatistic.getMetricDataList();
                    for (MetricData metricData : metricDataList) {
                        String average = metricData.getAverage();
                        double CPUavg = Double.parseDouble(average);
                        sumOfCPUUtilization += CPUavg;
                    }
                }
                double CPUUtilizationAvgOfCluster = sumOfCPUUtilization / nodes.size();
                log.info("클러스터 " + cluster.getClusterName() + "의 평균 CPU 사용률={}", CPUUtilizationAvgOfCluster);

                db.updateCPUUtilization(cluster.getClusterId(), CPUUtilizationAvgOfCluster);

                //auto-scale 처리
                if (cluster.isAutoScale()) {
                    //Scale-Out
                    if (nodes.size() < 5 && CPUUtilizationAvgOfCluster > 75) {
                        boolean flag = scaleService.checkIsNotCreatingInstance(cluster);
                        if (flag) continue; //생성중인 인스턴스가 있다면 Scale-Out처리하지 않는다.

                        log.info(cluster.getClusterName() + "을 scaleOut 처리합니다.");
                        ScaleOutResponse scaleOutResponse = scaleService.scaleOut(cluster);

                        //Scheduling : running 상태 확인 -> 비밀번호, port 작업 -> clustering
                        Node newNode = scaleOutResponse.getNewNode();
                        Node targetNode = scaleOutResponse.getTargetNode();
                        List<Node> nodesForScaleOut = scaleOutResponse.getNodes();
                        schedulerService.scaleOutScheduling(newNode, targetNode, nodesForScaleOut,cluster);

                    }
                    //Scale-In
                    else if (nodes.size() > 2 && CPUUtilizationAvgOfCluster < 15) {
                        log.info(cluster.getClusterName() + "을 scaleIn 처리합니다.");
                        Node deleteNode = scaleService.leaveCluster(cluster);
                        schedulerService.terminateNodeScheduling(deleteNode);
                    }
                }
            }else log.error("응답을 제대로 받지 못했습니다.");
        }
    }
}
