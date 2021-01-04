package com.icod.ilearning.services.protocol.course.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.data.model.Course;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGetCourseList {
    @JsonProperty("total")
    long total;
    @JsonProperty("perPage")
    long perPage;
    @JsonProperty("courses")
    List<Course> courses;
}
