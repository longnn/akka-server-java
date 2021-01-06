package com.icod.ilearning.services.protocol.user.find;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.services.protocol.base.RequestDataTableFind;
import lombok.Data;

@Data
public class RequestFindUsers extends RequestDataTableFind {
    @JsonProperty("filter")
    UserFilter filter;
}
