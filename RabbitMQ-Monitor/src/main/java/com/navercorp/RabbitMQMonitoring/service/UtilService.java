package com.navercorp.RabbitMQMonitoring.service;

import com.navercorp.RabbitMQMonitoring.config.ncloud.NcloudConfig;
import com.navercorp.RabbitMQMonitoring.domain.Node.Node;
import com.navercorp.RabbitMQMonitoring.domain.Port.Port;
import com.navercorp.RabbitMQMonitoring.dto.response.AllocatePort.AllocatePortResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.changeLoadBalancedServerInstances.ChangeLoadBalancedServerInstancesResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.createloadbalancer.CreateLoadBalancerResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.deleteloadbalancer.DeleteLoadBalancerResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList.MetricStatisticListResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.getRootPassword.PasswordResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.getServerInstance.GetServerInstanceResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.getLoadBalancerInstanceList.GetLoadBalancerInstanceListResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.portForwarding.PortConfigNoResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.stopCluster.StopClusterResponseDto;
import com.navercorp.RabbitMQMonitoring.dto.response.terminateCluster.TerminateClusterResponseDto;
import com.navercorp.RabbitMQMonitoring.util.ncloud.NcloudUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UtilService {
    private final NcloudConfig properties;
    private final NcloudUtil ncp;

    @Transactional
    public CreateClusterResponseDto createClusterRequest(String Uri) {
        log.info("createClusterRequest start............");

        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<CreateClusterResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, CreateClusterResponseDto.class);

        log.info("createClusterRequest finish............");
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    @Transactional
    public StopClusterResponseDto stopClusterRequest(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<StopClusterResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, StopClusterResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    public TerminateClusterResponseDto terminateClusterRequest(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<TerminateClusterResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, TerminateClusterResponseDto.class);
        log.info("요청 결과 : "+response.getStatusCode());
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }

    @Transactional
    public PasswordResponseDto getPasswordRequest(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<PasswordResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, PasswordResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }

    @Transactional
    public PortConfigNoResponseDto getPortConfigNoRequest(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<PortConfigNoResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, PortConfigNoResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }

    @Transactional
    public boolean allocatePortsRequest(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<AllocatePortResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, AllocatePortResponseDto.class);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Transactional
    public GetServerInstanceResponseDto getServerInstanceList(String Uri) {
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<GetServerInstanceResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, GetServerInstanceResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    @Transactional
    public UriComponentsBuilder commonUri(String service, String action) {
        return UriComponentsBuilder.fromUriString("/" + service + "/v2/" + action)
                .queryParam("responseFormatType", "json");
    }

    /**
     * getServerInstanceList , getPortForwardingRuleList
     */
    @Transactional
    public UriComponentsBuilder makeSignitureURI(String service, String action) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        return signitureURI;
    }

    /**
     * addPortForwardingRules
     */
    @Transactional
    public UriComponentsBuilder makeSignitureURI(String service, String action, String portForwardingConfigurationNo, List<Node> nodes, Port[] ports) {
        UriComponentsBuilder signitureURI = commonUri(service, action).queryParam("portForwardingConfigurationNo", portForwardingConfigurationNo);
        String portForwardingInternalPort = properties.getPortForwardingInternalPort();

        for (int i = 0; i < nodes.size(); i++) {
            Port p = ports[i];
            Node node = nodes.get(i);
            signitureURI
                    .queryParam("portForwardingRuleList." + (i + 1) + ".serverInstanceNo", node.getServerInstanceNo())
                    .queryParam("portForwardingRuleList." + (i + 1) + ".portForwardingExternalPort", p.getPortNum())
                    .queryParam("portForwardingRuleList." + (i + 1) + ".portForwardingInternalPort", portForwardingInternalPort);
        }
        return signitureURI;
    }


    /**
     * createServerInstances
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, int nodeCount, String nameNumber) {
        log.info("nameNumber={}",nameNumber);
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI
                .queryParam("serverCreateCount", nodeCount)
                .queryParam("serverImageProductCode", properties.getRabbitmqServerImageProductCode())
                .queryParam("serverProductCode", properties.getRabbitmqServerProductCode())
                .queryParam("serverName", "rabbit-" + nameNumber)
                .queryParam("accessControlGroupConfigurationNoList.1", properties.getGetAccessControlGroupConfigurationNo());
        return signitureURI;
    }


    /**
     * stopInstances, terminateInstances, 특정 getServerInstanceList
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, List<Node> nodes) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        for (int i = 0; i < nodes.size(); i++) {
            signitureURI.queryParam("serverInstanceNoList." + (i + 1), nodes.get(i).getServerInstanceNo());
        }
        return signitureURI;
    }


    /**
     * Only One For getServerInstanceState, terminate, stop Instance
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, Node node) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI.queryParam("serverInstanceNoList.1", node.getServerInstanceNo());
        return signitureURI;
    }


    /**
     * createLoadBalancer
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, String networkUsageTypeCode, List<Node> nodes, String protocolTypeCode, String loadBalancerPort, String serverPort) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI.queryParam("networkUsageTypeCode",networkUsageTypeCode)
                .queryParam("loadBalancerRuleList.1.protocolTypeCode",protocolTypeCode)
                .queryParam("loadBalancerRuleList.1.loadBalancerPort",loadBalancerPort)
                .queryParam("loadBalancerRuleList.1.serverPort",serverPort);

        for (int i = 0; i < nodes.size(); i++) {
            signitureURI.queryParam("serverInstanceNoList." + (i + 1), nodes.get(i).getServerInstanceNo());
        }
        return signitureURI;
    }

    /**
     * changeLoadBalancedServerInstances
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, String loadBalancerInstanceNo,Node newNode, List<Node> nodes) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI.queryParam("loadBalancerInstanceNo",loadBalancerInstanceNo);
        signitureURI.queryParam("serverInstanceNoList.1",newNode.getServerInstanceNo());
        int idx=1;
        for(Node node:nodes){
            if(node.getServerInstanceNo().equals(newNode.getServerInstanceNo())) continue;
            signitureURI.queryParam("serverInstanceNoList."+(++idx),node.getServerInstanceNo());
        }

        return signitureURI;
    }


    /**
     * get & stop & terminateInstance  또는 loadBalancerInstanceNo, deleteLoadBalancerInstances (1개)
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, String instanceNo) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        if(action.equals("getLoadBalancerInstanceList")|| action.equals("deleteLoadBalancerInstances")){
            signitureURI.queryParam("loadBalancerInstanceNoList.1", instanceNo);
        }else{
            signitureURI.queryParam("serverInstanceNoList.1", instanceNo);
        }
        return signitureURI;
    }


    public CreateLoadBalancerResponseDto createLoadBalancerRequest(String Uri) {
        log.info("createLoadBalancerRequest start............");

        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<CreateLoadBalancerResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, CreateLoadBalancerResponseDto.class);

        log.info("createLoadBalancerRequest finish............");
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    public DeleteLoadBalancerResponseDto deleteLoadBalancerRequest(String Uri) {
        log.info("deleteLoadBalancerRequest start............");

        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<DeleteLoadBalancerResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, DeleteLoadBalancerResponseDto.class);

        log.info("deleteLoadBalancerRequest finish............");
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }

    public ChangeLoadBalancedServerInstancesResponseDto changeLoadBalancedServerInstancesRequest(String Uri) {
        log.info("changeLoadBalancedServerInstancesRequest start............");

        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        log.info("\nRequest URI="+RequestURI+"\nRequest Headers="+httpEntity.getHeaders());

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<ChangeLoadBalancedServerInstancesResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, ChangeLoadBalancedServerInstancesResponseDto.class);

        log.info("changeLoadBalancedServerInstancesRequest finish............");
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    /**
     * addPortForwardingRules
     */
    @Transactional
    public UriComponentsBuilder makeSignitureURI(String service, String action, String portForwardingConfigurationNo, Node node) {
        UriComponentsBuilder signitureURI = commonUri(service, action).queryParam("portForwardingConfigurationNo", portForwardingConfigurationNo);
        String portForwardingInternalPort = properties.getPortForwardingInternalPort();

            signitureURI
                    .queryParam("portForwardingRuleList.1.serverInstanceNo", node.getServerInstanceNo())
                    .queryParam("portForwardingRuleList.1.portForwardingExternalPort", node.getPortForwardingPort())
                    .queryParam("portForwardingRuleList.1.portForwardingInternalPort", portForwardingInternalPort);
        return signitureURI;
    }


    @Transactional
    public GetServerInstanceResponseDto getServerInstanceList() {
        String Uri = makeSignitureURI("server", "getServerInstanceList").toUriString();
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<GetServerInstanceResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, GetServerInstanceResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }


    public GetLoadBalancerInstanceListResponseDto getLoadBalancerInstanceList(String LoadBalancerInstanceNo) throws Exception {
        String Uri = makeSignitureURI("loadbalancer", "getLoadBalancerInstanceList",LoadBalancerInstanceNo).toUriString();
        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<GetLoadBalancerInstanceListResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, GetLoadBalancerInstanceListResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
    }

    public MetricStatisticListResponseDto getMetricStatisticList(List<Node> nodes) throws URISyntaxException {
        log.info("start getMetricStatisticList...............");

        SimpleDateFormat form = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ");
        long now=System.currentTimeMillis();
        String startTime=form.format(now-600000);
        String endTime = form.format(now);
        String period = "60";

        URIBuilder builder = new URIBuilder();
        builder.setScheme(null).setHost(null).setPath("/monitoring/v2/getMetricStatisticList")
                .setParameter("responseFormatType", "json")
                .setParameter("metricName", "CPUUtilization")
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .setParameter("period", period);

        for(int i=0;i<nodes.size();i++){
            Node node=nodes.get(i);
            builder.setParameter("instanceNoList." + (i + 1), node.getServerInstanceNo());
        }
        String Uri = builder.build().toString();

        HttpEntity<String> httpEntity = ncp.getHttpEntity("GET", Uri, properties.getAccessKey(), properties.getSecretKey());
        String RequestURI = properties.getBaseURL() + Uri;

        RestTemplate restTemplate = ncp.restTemplate();
        ResponseEntity<MetricStatisticListResponseDto> response = restTemplate.exchange(URI.create(RequestURI), HttpMethod.GET, httpEntity, MetricStatisticListResponseDto.class);
        return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;

    }

    public boolean isRunningNodes(List<Node> nodes) {
        for(Node node:nodes){
            if(!node.getServerStatusCode().equals("RUN")){
                log.info("아직 "+node.getServerName()+"이 "+node.getServerStatusCode()+"상태 입니다...");
                return false;
            }
        }
        return true;
    }
}
