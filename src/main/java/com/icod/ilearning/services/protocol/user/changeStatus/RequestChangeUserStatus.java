package com.icod.ilearning.services.protocol.user.changeStatus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequestChangeUserStatus {
    @JsonProperty("ids")
    List<Long> ids;
    @JsonProperty("status")
    String status;
}
