package com.icod.ilearning.services.protocol.user.find;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.data.model.User;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGetUserList {
    @JsonProperty("total")
    long total;
    @JsonProperty("items")
    List<User> users;
}
