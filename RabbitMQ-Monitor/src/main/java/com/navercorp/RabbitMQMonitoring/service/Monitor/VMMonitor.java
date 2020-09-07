package com.navercorp.RabbitMQMonitoring.service.Monitor;

import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.dto.response.getServerInstance.GetServerInstanceResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstance;
import com.navercorp.RabbitMQMonitoring.dto.response.serverInstance.ServerInstanceStatus;
import com.navercorp.RabbitMQMonitoring.service.DBService;
import com.navercorp.RabbitMQMonitoring.service.ResourceService;
import com.navercorp.RabbitMQMonitoring.service.SchedulerService;
import com.navercorp.RabbitMQMonitoring.service.UtilService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class VMMonitor {
    static final String MyServerInstanceNo="4999537";
    private final DBService db;
    private final UtilService utilService;
    private final SchedulerService schedulerService;
    private final ResourceService ResourceService;

    private static Map<String,Node> nodes=new HashMap<>();


    @Scheduled(fixedRate = 30000)
    public void getServerInstanceList() {
        ResourceService.ResourceUpdate();
        log.info("getServerInstanceList......");
        GetServerInstanceResponseDto responseDto = utilService.getServerInstanceList();

        Map<String, Node> checkNcpToDataBase=new HashMap<>();// 원격 서버 정보(상태관리용, 추적용)
        Map<String, Node> checkDataBaseToNcp=new HashMap<>();// 데이터 베이스 정보(데이터베이스 동기화용)

        for(String serverInstanceNo:nodes.keySet()){
            checkNcpToDataBase.put(serverInstanceNo,nodes.get(serverInstanceNo));
        }

        List<Node> dbnodes=db.findAllNodes();
        for(Node node:dbnodes){
            checkDataBaseToNcp.put(node.getServerInstanceNo(),node);
        }


        if(responseDto!=null){
            ServerInstance[] serverInstanceList = responseDto.getGetServerInstanceResponse().getServerInstanceList();
            for(ServerInstance serverInstance:serverInstanceList){
                ServerInstanceStatus serverInstanceStatus=serverInstance.getServerInstanceStatus();
                String curStatusCode=serverInstanceStatus.getCode();
                String curStatusCodeName=serverInstanceStatus.getCodeName();

                // 요청 이전 상태의 노드를 가져온다
                String serverInstanceNo=serverInstance.getServerInstanceNo();
                if(serverInstanceNo.equals(MyServerInstanceNo)) continue;
                checkDataBaseToNcp.remove(serverInstanceNo);

                Node prevNode=nodes.get(serverInstanceNo);
                //이전에 없던 노드라면 상태 관리에 등록
                if(prevNode==null){
                    log.info(serverInstanceNo + " : new Server ----->"+ curStatusCode);
                    Node node=db.findNodeByNodeInstanceNo(serverInstanceNo);

                    if(node==null){ //인스턴스는 존재하나, DB에 저장되지 않은 상황일 때 원격서버 삭제 요청.
                        boolean stoppable=schedulerService.checkCanStoppable(serverInstanceNo);
                        if(!stoppable) continue;

                        log.info("DB화 되지 않은 인스턴스를 종료합니다.------>"+serverInstanceNo);
                        schedulerService.terminateUnSavedNodeScheduling(serverInstanceNo);
                    }else{
                        nodes.put(serverInstanceNo, node);
                    }
                }else { // 상태 업데이트
                    checkNcpToDataBase.remove(serverInstanceNo);

                    String prevStatusCode = prevNode.getServerStatusCode();
                    if (prevStatusCode.equals(curStatusCode)) continue;

                    log.info(serverInstanceNo + ":ServerStatus changed " + prevStatusCode + " to " + curStatusCode);
                    db.setServerStatusByServerInstanceNo(serverInstanceNo, curStatusCode, curStatusCodeName);

                    Node node=nodes.get(serverInstanceNo);
                    node.setServerStatus(curStatusCode,curStatusCodeName);
                    nodes.put(serverInstanceNo,node);
               }
            }

            //인스턴스가 반납이 된 상황
            if(checkNcpToDataBase.size()!=0) {
                for(String serverInstanceNo:checkNcpToDataBase.keySet()){
                    nodes.remove(serverInstanceNo);
                    log.info(serverInstanceNo+"가 반납처리 되었습니다..");

                    // 만약 DB 처리가 되지 않았을 때
                    Node terminatedNode=db.findNodeByNodeInstanceNo(serverInstanceNo);
                    if(terminatedNode!=null) db.deleteNode(terminatedNode);
                }
            }

            if(checkDataBaseToNcp.size()!=0){
                for(String serverInstanceNo:checkDataBaseToNcp.keySet()){
                    log.info(serverInstanceNo+"는 잘못 저장된 Data입니다. 삭제합니다.");

                    Node deleteNode=checkDataBaseToNcp.get(serverInstanceNo);
                    db.deleteNode(deleteNode);
                }
            }

        }
    }
}
