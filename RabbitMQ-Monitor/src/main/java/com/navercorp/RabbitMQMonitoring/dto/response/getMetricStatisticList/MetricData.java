package com.navercorp.RabbitMQMonitoring.dto.response.getMetricStatisticList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetricData {

    @JsonProperty(value = "average")
    private String average;
}
