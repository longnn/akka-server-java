package com.icod.ilearning.services.protocol.user.checkEmail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseCheckEmail {
    @JsonProperty("isExisted")
    boolean isExisted;
}
