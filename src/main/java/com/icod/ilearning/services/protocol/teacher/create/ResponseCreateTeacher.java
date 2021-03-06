package com.icod.ilearning.services.protocol.teacher.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCreateTeacher {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
