package com.icod.ilearning.services.protocol.course.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.data.model.CourseModel;
import com.icod.ilearning.data.model.UserModel;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGetCourseList {
    @JsonProperty("total")
    long total;
    @JsonProperty("perPage")
    long perPage;
    @JsonProperty("courses")
    List<CourseModel> courses;
}
