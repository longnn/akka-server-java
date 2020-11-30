package com.icod.ilearning.services.protocol.role.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCreateRole {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
