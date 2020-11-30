package com.icod.ilearning.services.protocol.role.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateRole {
    @JsonProperty("name")
    String name;
    @JsonProperty("status")
    int status;
}
