package com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricStatisticListResponse {

    @JsonProperty(value = "returnMessage")
    private String returnMessage;

    @JsonProperty(value = "metricStatisticList")
    private List<MetricStatistic> metricStatisticList;
}
