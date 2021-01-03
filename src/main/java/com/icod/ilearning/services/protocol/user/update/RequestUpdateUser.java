package com.icod.ilearning.services.protocol.user.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateUser {
    @JsonProperty("fullname")
    String fullname;
    @JsonProperty("password")
    String password;
}
