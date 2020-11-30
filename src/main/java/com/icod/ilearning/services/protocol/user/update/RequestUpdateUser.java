package com.icod.ilearning.services.protocol.user.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateUser {
    @JsonProperty("name")
    String name;
    @JsonProperty("password")
    String password;
}
