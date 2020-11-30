package com.icod.ilearning.services.protocol.auth.login;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ResponseLogin {
    @JsonProperty("accessToken")
    String accessToken;

    @JsonProperty("refreshToken")
    String refreshToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    @JsonProperty("expiresIn")
    Date expiresIn;
}
