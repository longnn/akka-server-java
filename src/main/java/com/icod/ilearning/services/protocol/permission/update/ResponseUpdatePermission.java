package com.icod.ilearning.services.protocol.permission.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUpdatePermission {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
