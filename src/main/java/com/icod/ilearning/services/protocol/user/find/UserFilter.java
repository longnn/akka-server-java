package com.icod.ilearning.services.protocol.user.find;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserFilter {
    @JsonProperty("status")
    String status;
    @JsonProperty("role")
    String role;
}
