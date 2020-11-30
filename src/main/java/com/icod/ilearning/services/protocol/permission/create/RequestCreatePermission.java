package com.icod.ilearning.services.protocol.permission.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestCreatePermission {
    @JsonProperty("name")
    String name;
    @JsonProperty("status")
    int status;
}
