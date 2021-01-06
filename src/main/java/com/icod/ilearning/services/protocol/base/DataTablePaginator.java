package com.icod.ilearning.services.protocol.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataTablePaginator {
    @JsonProperty("page")
    int page;
    @JsonProperty("pageSize")
    int pageSize;
    @JsonProperty("total")
    int total;
    @JsonProperty("pageSizes")
    int[] pageSizes;
}
