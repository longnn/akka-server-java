package com.icod.ilearning.services.protocol.permission.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdatePermission {
    @JsonProperty("name")
    String name;
    @JsonProperty("status")
    int status;
}
