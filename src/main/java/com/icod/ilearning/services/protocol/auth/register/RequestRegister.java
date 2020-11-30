package com.icod.ilearning.services.protocol.auth.register;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestRegister {
    @JsonProperty("name")
    String name;
    @JsonProperty("email")
    String email;
    @JsonProperty("password")
    String password;
}
