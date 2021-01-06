package com.icod.ilearning.services.protocol.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Set;
@Data
public class DataTableGrouping {
    @JsonProperty("itemIds")
    int[] itemIds;
}
