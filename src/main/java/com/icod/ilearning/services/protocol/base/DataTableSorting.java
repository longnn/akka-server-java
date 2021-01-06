package com.icod.ilearning.services.protocol.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DataTableSorting {
    @JsonProperty("column")
    String column;
    @JsonProperty("direction")
    String direction;
}
