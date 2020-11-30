package com.icod.ilearning.services.protocol.role.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.icod.ilearning.data.model.RoleModel;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({"total","per_page","items"})
public class ResponseGetRoleList {
    @JsonProperty("total")
    Integer total;
    @JsonProperty("per_page")
    Integer perPage;
    @JsonProperty("items")
    List<RoleModel> roles;
}
