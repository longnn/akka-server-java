package com.icod.ilearning.services.protocol.role.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestCreateRole {
    @JsonProperty("name")
    String name;
    @JsonProperty("status")
    int status;
}
