package com.icod.ilearning.services.protocol.role.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RequestGetRoleList {
    @JsonProperty("name")
    String name;
    @JsonProperty("limit")
    Integer limit;
    @JsonProperty("offset")
    Integer offset;
}
