package com.navercorp.RabbitMQMonitoring.dto.response.getRootPassword;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResponseDto {
    @JsonProperty(value = "getRootPasswordResponse")
    private PasswordResponse passwordResponse;
}
