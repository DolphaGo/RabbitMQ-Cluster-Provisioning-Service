package com.navercorp.rabbit.service;

import com.navercorp.rabbit.config.ncloud.NcloudConfig;
import com.navercorp.rabbit.util.ncloud.NcloudUtil;
import com.navercorp.rabbit.domain.Node.Node;
import com.navercorp.rabbit.domain.Port.Port;
import com.navercorp.rabbit.dto.response.AllocatePort.AllocatePortResponseDto;
import com.navercorp.rabbit.dto.response.changeLoadBalancedServerInstances.ChangeLoadBalancedServerInstancesResponseDto;
import com.navercorp.rabbit.dto.response.createCluster.CreateClusterResponseDto;
import com.navercorp.rabbit.dto.response.createloadbalancer.CreateLoadBalancerResponseDto;
import com.navercorp.rabbit.dto.response.deleteloadbalancer.DeleteLoadBalancerResponseDto;
import com.navercorp.rabbit.dto.response.getRootPassword.PasswordResponseDto;
import com.navercorp.rabbit.dto.response.getServerInstance.GetServerInstanceResponseDto;
import com.navercorp.rabbit.dto.response.portForwarding.PortConfigNoResponseDto;
import com.navercorp.rabbit.dto.response.stopCluster.StopClusterResponseDto;
import com.navercorp.rabbit.dto.response.terminateCluster.TerminateClusterResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.net.URI;
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
    public StopClusterResponseDto stopClusterRequest(String Uri) throws InterruptedException {
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
    public UriComponentsBuilder makeSignitureURI(String service, String action,String networkUsageTypeCode,List<Node> nodes,String protocolTypeCode,String loadBalancerPort, String serverPort) {
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
    public UriComponentsBuilder makeSignitureURI(String service, String action,String loadBalancerInstanceNo,Node newNode, List<Node> nodes) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI.queryParam("loadBalancerInstanceNo",loadBalancerInstanceNo);
        signitureURI.queryParam("serverInstanceNoList.1",newNode.getServerInstanceNo());
        for(int i=0;i<nodes.size();i++){
            Node node=nodes.get(i);
            signitureURI.queryParam("serverInstanceNoList."+(i+2),node.getServerInstanceNo());
        }

        return signitureURI;
    }


    /**
     * deleteLoadBalancer
     */
    public UriComponentsBuilder makeSignitureURI(String service, String action, String loadBalancerInstanceNo) {
        UriComponentsBuilder signitureURI = commonUri(service, action);
        signitureURI.queryParam("loadBalancerInstanceNoList.1", loadBalancerInstanceNo);
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
    public UriComponentsBuilder makeSignitureURI(String service, String action, String portForwardingConfigurationNo, Node node, String portNum) {
        UriComponentsBuilder signitureURI = commonUri(service, action).queryParam("portForwardingConfigurationNo", portForwardingConfigurationNo);
        String portForwardingInternalPort = properties.getPortForwardingInternalPort();

            signitureURI
                    .queryParam("portForwardingRuleList.1.serverInstanceNo", node.getServerInstanceNo())
                    .queryParam("portForwardingRuleList.1.portForwardingExternalPort", portNum)
                    .queryParam("portForwardingRuleList.1.portForwardingInternalPort", portForwardingInternalPort);
        return signitureURI;
    }

    public UriComponentsBuilder makeAllocatePortsSignitureURI(String service, String action, String portForwardingConfigurationNo, List<Node> nodes) {
        UriComponentsBuilder signitureURI = commonUri(service, action).queryParam("portForwardingConfigurationNo", portForwardingConfigurationNo);
        String portForwardingInternalPort = properties.getPortForwardingInternalPort();

        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            String portNum=node.getPortForwardingPort();
            signitureURI
                    .queryParam("portForwardingRuleList." + (i + 1) + ".serverInstanceNo", node.getServerInstanceNo())
                    .queryParam("portForwardingRuleList." + (i + 1) + ".portForwardingExternalPort", portNum)
                    .queryParam("portForwardingRuleList." + (i + 1) + ".portForwardingInternalPort", portForwardingInternalPort);
        }
        return signitureURI;
    }
}
