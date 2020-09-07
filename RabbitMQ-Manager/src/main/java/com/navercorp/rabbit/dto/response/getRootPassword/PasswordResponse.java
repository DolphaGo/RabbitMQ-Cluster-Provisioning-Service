package com.navercorp.rabbit.dto.response.getRootPassword;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResponse {
    @JsonProperty(value = "returnMessage")
    String returnMessage;
    @JsonProperty(value = "rootPassword")
    String rootPassword;
}
