package com.icod.ilearning.services.protocol.auth.refreshToken;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestRefreshToken {
    @JsonProperty("refreshToken")
    String refreshToken;
}
