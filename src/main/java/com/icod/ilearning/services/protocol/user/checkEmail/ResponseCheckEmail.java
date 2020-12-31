package com.icod.ilearning.services.protocol.user.checkEmail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResponseCheckEmail {
    @JsonProperty("isExisted")
    boolean isExisted;
}
