package com.navercorp.rabbit.dto.response.serverInstance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerInstancesResponse {

    @JsonProperty(value = "returnMessage")
    private String returnMessage;

    @JsonProperty(value = "totalRows")
    private int totalRows;

    @JsonProperty(value = "serverInstanceList")
    private ServerInstance[] serverInstanceList;
}
