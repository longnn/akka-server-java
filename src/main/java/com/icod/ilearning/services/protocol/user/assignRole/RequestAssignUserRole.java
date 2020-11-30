package com.icod.ilearning.services.protocol.user.assignRole;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestAssignUserRole {
    @JsonProperty("role_ids")
    Long[] roleIds;
}
