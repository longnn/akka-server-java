package com.icod.ilearning.services.protocol.teacher.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateTeacher {
    @JsonProperty("firstName")
    String firstName;
    @JsonProperty("lastName")
    String lastName;
    @JsonProperty("email")
    String email;
    @JsonProperty("phone")
    String phone;
    @JsonProperty("gender")
    int gender;
    @JsonProperty("status")
    int status;
}
