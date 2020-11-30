package com.icod.ilearning.services.protocol.permission.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.icod.ilearning.data.model.PermissionModel;
import com.icod.ilearning.data.model.RoleModel;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"total","per_page","items"})
public class ResponseGetPermissionList {
    @JsonProperty("total")
    long total;
    @JsonProperty("per_page")
    long perPage;
    @JsonProperty("items")
    List<PermissionModel> permissions;
}
