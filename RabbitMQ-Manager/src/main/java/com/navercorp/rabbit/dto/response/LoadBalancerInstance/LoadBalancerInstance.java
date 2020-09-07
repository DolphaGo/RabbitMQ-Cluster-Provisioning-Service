package com.navercorp.rabbit.dto.response.LoadBalancerInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadBalancerInstance {

    @JsonProperty(value = "loadBalancerInstanceNo")
    private String loadBalancerInstanceNo;

    @JsonProperty(value = "virtualIp")
    private String virtualIp;

    @JsonProperty(value = "loadBalancerName")
    private String loadBalancerName;

    @JsonProperty(value = "loadBalancerAlgorithmType")
    private CommonCode loadBalancerAlgorithmType;

    @JsonProperty(value = "domainName")
    private String domainName;

    @JsonProperty(value = "internetLineType")
    private CommonCode internetLineType;

    @JsonProperty(value = "loadBalancerInstanceStatusName")
    private String loadBalancerInstanceStatusName;

    @JsonProperty(value = "loadBalancerInstanceStatus")
    private CommonCode loadBalancerInstanceStatus;

    @JsonProperty(value = "loadBalancerInstanceOperation")
    private CommonCode loadBalancerInstanceOperation;

    @JsonProperty(value = "networkUsageType")
    private CommonCode networkUsageType;

    @JsonProperty(value = "isHttpKeepAlive")
    private boolean isHttpKeepAlive;

    @JsonProperty(value = "connectionTimeout")
    private Integer connectionTimeout;

    @JsonProperty(value = "loadBalancerRuleList")
    private List<LoadBalancerRule> loadBalancerRuleList;

    @JsonProperty(value = "loadBalancedServerInstanceList")
    private List<LoadBalancedServerInstance> loadBalancedServerInstanceList;

}
