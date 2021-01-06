package com.icod.ilearning.services.protocol.user.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateUser {
    @JsonProperty("fullname")
    String fullname;
    @JsonProperty("email")
    String email;
    @JsonProperty("password")
    String password;
    @JsonProperty("status")
    int status;
    @JsonProperty("role")
    int role;
}
