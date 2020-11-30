package com.icod.ilearning.services.protocol.role.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUpdateRole {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
