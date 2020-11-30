package com.icod.ilearning.services.protocol.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestLogin {
    @JsonProperty("email")
    String email;
    @JsonProperty("password")
    String password;
}
