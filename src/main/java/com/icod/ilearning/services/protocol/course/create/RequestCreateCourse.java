package com.icod.ilearning.services.protocol.course.create;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RequestCreateCourse {
    @JsonProperty("name")
    String name;
    @JsonProperty("title")
    String title;
    @JsonProperty("imageUrl")
    String imageUrl;
    @JsonProperty("videoUrl")
    String videoUrl;
    @JsonProperty("description")
    String description;
    @JsonProperty("status")
    int status;
}
