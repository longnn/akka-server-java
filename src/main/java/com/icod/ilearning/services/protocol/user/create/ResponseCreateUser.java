package com.icod.ilearning.services.protocol.user.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCreateUser {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
