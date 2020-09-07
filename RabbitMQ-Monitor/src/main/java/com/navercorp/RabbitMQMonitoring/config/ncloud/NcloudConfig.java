package com.navercorp.RabbitMQMonitoring.config.ncloud;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@Getter
@Configuration
public class NcloudConfig {

    @Value("${ncloud.baseURL}")
    private String baseURL;

    @Value("${ncloud.accessKey}")
    private String accessKey;

    @Value("${ncloud.secretKey}")
    private String secretKey;

    @Value("${ncloud.rabbitmq.serverProductCode}")
    private String rabbitmqServerProductCode;

    @Value("${ncloud.rabbitmq.serverImageProductCode}")
    private String rabbitmqServerImageProductCode;

    @Value("${ncloud.portForwardingConfigurationNo}")
    private String portForwardingConfigurationNo;

    @Value("${ncloud.portForwardingInternalPort}")
    private String portForwardingInternalPort;

    @Value("${ncloud.getAccessControlGroupConfigurationNo}")
    private String getAccessControlGroupConfigurationNo;

    @Value("${ncloud.ns_secret}")
    private String NS_secret;
}
