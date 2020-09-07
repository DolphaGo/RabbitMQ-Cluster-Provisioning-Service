package com.navercorp.rabbit.controller;

import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.dto.request.scale.AutoScaleRequestDto;
import com.navercorp.rabbit.dto.response.client.ClusterInfoResponseDto;
import com.navercorp.rabbit.dto.response.client.FindMyClustersResponseDto;
import com.navercorp.rabbit.dto.response.createloadbalancer.CreateLoadBalancerResponse;
import com.navercorp.rabbit.dto.response.deleteloadbalancer.DeleteLoadBalancerResponse;
import com.navercorp.rabbit.dto.response.serverInstance.ServerInstancesResponse;
import com.navercorp.rabbit.service.ClientService;
import com.navercorp.rabbit.dto.request.scale.ScaleInRequestDto;
import com.navercorp.rabbit.dto.request.createCluster.CreateClusterRequestDto;
import com.navercorp.rabbit.dto.request.createLoadBalancer.CreateLoadBalancerRequestDto;
import com.navercorp.rabbit.dto.request.deleteLoadBalancer.DeleteLoadBalancerRequestDto;
import com.navercorp.rabbit.dto.request.scale.ScaleOutRequestDto;
import com.navercorp.rabbit.dto.request.stopCluster.StopClusterRequestDto;
import com.navercorp.rabbit.dto.request.terminateCluster.TerminateClusterRequestDto;
import com.navercorp.rabbit.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.rabbit.dto.response.stopCluster.StopClusterResponseDto;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api")
public class ManageController {
    private final ClientService clientService;

    @ApiOperation("createCluster by using clusterName")
    @PostMapping("/createCluster")
    public ResponseEntity createCluster(@RequestBody CreateClusterRequestDto createClusterRequestDto) throws Exception {
        int nodeCount = createClusterRequestDto.getNodeCount();
        CreateClusterResponseDto response = clientService.createClusterRequest(nodeCount);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("terminateCluster by using clusterName")
    @DeleteMapping("/terminateCluster")
    public ResponseEntity terminateCluster(@RequestBody TerminateClusterRequestDto terminateClusterRequestDto) {
        String clusterName = terminateClusterRequestDto.getClusterName();
        clientService.terminateClusterRequest(clusterName);
        String message=clusterName+"을 삭제하는 요청을 보냈습니다.";
        return ResponseEntity.ok(message);
    }

    @ApiOperation("createLoadBalancer by using clusterName")
    @PutMapping("/createLoadBalancer")
    public ResponseEntity createLoadBalancer(@RequestBody CreateLoadBalancerRequestDto createLoadBalancerRequestDto){
        String clusterName=createLoadBalancerRequestDto.getClusterName();
        clientService.createLoadBalancer(clusterName);
        String message=clusterName+"에 loadBalancer 설정 요청을 보냈습니다.";
        return ResponseEntity.ok(message);
    }

    @ApiOperation("deleteLoadBalancer by using clusterName")
    @DeleteMapping("/deleteLoadBalancer")
    public ResponseEntity deleteLoadBalancer(@RequestBody DeleteLoadBalancerRequestDto deleteLoadBalancerRequestDto){
        String clusterName=deleteLoadBalancerRequestDto.getClusterName();
        DeleteLoadBalancerResponse response= clientService.deleteLoadBalancer(clusterName);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("manual-scaleIn by using clusterName")
    @PostMapping("/scaleIn")
    public ResponseEntity scaleIn(@RequestBody ScaleInRequestDto scaleInRequestDto) throws Exception{
        String clusterName=scaleInRequestDto.getClusterName();
        Node response=clientService.scaleIn(clusterName);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("manual-scaleOut by using clusterName")
    @PostMapping("/scaleOut")
    public ResponseEntity scaleOut(@RequestBody ScaleOutRequestDto scaleOutRequestDto) throws Exception{
        String clusterName=scaleOutRequestDto.getClusterName();
        ServerInstancesResponse response=clientService.scaleOut(clusterName);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("use auto-scaling by using clusterName")
    @PutMapping("/onAutoScale")
    public ResponseEntity onAutoScale(@RequestBody AutoScaleRequestDto autoScaleRequestDto){
        String clusterName=autoScaleRequestDto.getClusterName();
        clientService.onAutoScale(clusterName);

        String message = "AutoScale 기능 활성화 :" +clusterName;
        return ResponseEntity.ok(message);
    }

    @ApiOperation("unuse auto-scaling by using clusterName")
    @PutMapping("/offAutoScale")
    public ResponseEntity offAutoScale(@RequestBody AutoScaleRequestDto autoScaleRequestDto){
        String clusterName=autoScaleRequestDto.getClusterName();
        clientService.offAutoScale(clusterName);
        String message = "AutoScale 기능 비활성화 :" +clusterName;
        return ResponseEntity.ok(message);
    }

    @ApiOperation("Provide the detail of the cluster you have.")
    @GetMapping("/detail/{clusterName}")
    public ClusterInfoResponseDto detailInfoOfCluster(@PathVariable String clusterName) {
        return clientService.detailInfoOfCluster(clusterName);
    }

    @ApiOperation("Provide the names of the clusters you have.")
    @GetMapping("/findClusters")
    public FindMyClustersResponseDto findClusters() {
        return clientService.findAllClusters();
    }

}
