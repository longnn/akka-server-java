package com.icod.ilearning.services.protocol.user.assignPermission;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestAssignUserPermission {
    @JsonProperty("per_ids")
    Long[] permissionIds;
}
