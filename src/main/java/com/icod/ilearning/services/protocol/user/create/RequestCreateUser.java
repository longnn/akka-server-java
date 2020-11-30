package com.icod.ilearning.services.protocol.user.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestCreateUser {
    @JsonProperty("name")
    String name;
    @JsonProperty("email")
    String email;
    @JsonProperty("password")
    String password;
    @JsonProperty("status")
    int status;
}
