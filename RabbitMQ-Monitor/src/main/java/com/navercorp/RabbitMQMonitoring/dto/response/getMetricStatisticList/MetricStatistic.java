package com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricStatistic {

    @JsonProperty(value = "instanceNo")
    private String instanceNo;

    @JsonProperty(value = "metricDataList")
    private List<MetricData> metricDataList;
}
