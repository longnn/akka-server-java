package com.icod.ilearning.services.protocol.user.deleteUsers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RequestDeleteUsers {
    @JsonProperty("ids")
    List<Long> ids;
}
