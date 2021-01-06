package com.icod.ilearning.services.protocol.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestDataTableFind {
    @JsonProperty("paginator")
    DataTablePaginator paginator;
    @JsonProperty("sorting")
    DataTableSorting sorting;
    @JsonProperty("searchTerm")
    String searchTerm;
    @JsonProperty("grouping")
    DataTableGrouping grouping;
}
