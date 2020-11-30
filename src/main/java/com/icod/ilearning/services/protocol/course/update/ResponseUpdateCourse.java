package com.icod.ilearning.services.protocol.course.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUpdateCourse {
    @JsonProperty("error_code")
    int errorCode;
    @JsonProperty("message")
    String message;
}
