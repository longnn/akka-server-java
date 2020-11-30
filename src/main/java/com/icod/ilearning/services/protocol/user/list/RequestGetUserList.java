package com.icod.ilearning.services.protocol.user.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class RequestGetUserList {
    @JsonProperty("name")
    String name;
    @JsonProperty("role")
    String role;
    @JsonProperty("email")
    String email;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    Date createdAt;
    @JsonProperty("limit")
    Long limit;
    @JsonProperty("offset")
    Long offset;
}
