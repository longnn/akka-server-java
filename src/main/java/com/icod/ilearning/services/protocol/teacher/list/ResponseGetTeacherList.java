package com.icod.ilearning.services.protocol.teacher.list;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.icod.ilearning.data.model.Teacher;
import lombok.Data;

import java.util.List;

@Data
public class ResponseGetTeacherList {
    @JsonProperty("total")
    long total;
    @JsonProperty("perPage")
    long perPage;
    @JsonProperty("teachers")
    List<Teacher> teachers;
}
