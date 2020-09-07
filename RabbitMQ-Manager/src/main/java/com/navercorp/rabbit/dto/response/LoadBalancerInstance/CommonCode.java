package com.navercorp.rabbit.dto.response.LoadBalancerInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonCode {

    @JsonProperty(value = "code")
    String code;

    @JsonProperty(value = "codeName")
    String codeName;
}
