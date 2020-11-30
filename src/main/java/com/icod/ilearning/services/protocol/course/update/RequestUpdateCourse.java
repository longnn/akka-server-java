package com.icod.ilearning.services.protocol.course.update;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestUpdateCourse {
    @JsonProperty("title")
    String title;
    @JsonProperty("description")
    String description;
}
