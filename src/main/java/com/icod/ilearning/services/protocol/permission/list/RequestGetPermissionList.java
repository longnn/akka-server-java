package com.icod.ilearning.services.protocol.permission.list;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class RequestGetPermissionList {
    @JsonProperty("name")
    String name;
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    Date createdAt;
    @JsonProperty("limit")
    int limit;
    @JsonProperty("offset")
    int offset;
}
