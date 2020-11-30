package com.icod.ilearning.services.protocol.user.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.data.model.UserModel;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGetUserList {
    @JsonProperty("total")
    long total;
    @JsonProperty("perPage")
    long perPage;
    @JsonProperty("users")
    List<UserModel> users;
}
