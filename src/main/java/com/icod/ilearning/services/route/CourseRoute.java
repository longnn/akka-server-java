package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Rejections;
import akka.http.javadsl.server.Route;
import com.github.slugify.Slugify;
import com.icod.ilearning.data.dao.CourseDao;
import com.icod.ilearning.data.model.Course;
import com.icod.ilearning.services.protocol.course.create.RequestCreateCourse;
import com.icod.ilearning.services.protocol.course.list.RequestGetCourseList;
import com.icod.ilearning.services.protocol.course.list.ResponseGetCourseList;
import com.icod.ilearning.services.protocol.course.update.RequestUpdateCourse;
import com.icod.ilearning.util.SecurityUtil;
import com.icod.ilearning.util.StringUtil;
import com.icod.ilearning.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CourseRoute extends AllDirectives {
    final CourseDao courseDao;
    public CourseRoute(){
        courseDao = new CourseDao();
    }
    public Route createRoute() {
        return  pathEnd(() ->
                get(()-> entity(Jackson.unmarshaller(RequestGetCourseList.class), request-> getCourse(request))).orElse(
                        post(()-> entity(Jackson.unmarshaller(RequestCreateCourse.class), request-> createCourse(request))))
        ).orElse(
                path(PathMatchers.longSegment(), id->
                        get(()-> getCourse(id)).orElse(
                                put(()-> updateCourse(id)).orElse(
                                        delete(()-> deleteCourse(id))))
                ));
    }
    private Route getCourse(RequestGetCourseList request){
        CompletableFuture<ResponseGetCourseList> future = CompletableFuture.supplyAsync(() -> {
            List<Course> courses = courseDao.getAll(request.getName());
            ResponseGetCourseList response = new ResponseGetCourseList();
            response.setTotal(courses.size());
            response.setCourses(courses);
            response.setPerPage(request.getLimit());
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }
    private Route getCourse(long id){
        Course course = courseDao.findById(id);
        if(course==null){
            return complete(StatusCodes.NOT_FOUND,"course not found");
        }
        return complete(StatusCodes.OK,course,Jackson.marshaller());
    }
    private Route createCourse(RequestCreateCourse request) {
        // VALIDATE
        if(ValidationUtil.isNullOrEmpty(request.getName())){
            return reject(Rejections.malformedFormField("title","title required"));
        }
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setSlug(new Slugify().slugify(StringUtil.removeAccent(request.getTitle())));
        course.setImageUrl(request.getImageUrl());
        course.setVideoUrl(request.getVideoUrl());
        course.setCreatedAt(new Date());
        course.setUpdatedAt(new Date());
        Long id = courseDao.insert(course);
        if(id == null){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to create course");
        }else {
            return complete(StatusCodes.OK,"course create success");
        }
    }
    private Route updateCourse(long id){
        Course course = courseDao.findById(id);
        if(course==null){
            return complete(StatusCodes.NOT_FOUND,"course not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdateCourse.class), request -> {
            if(!ValidationUtil.isNullOrEmpty(request.getTitle())){
                course.setTitle(request.getTitle());
            }
            if(ValidationUtil.isNullOrEmpty(request.getDescription())){
                course.setDescription(SecurityUtil.md5(request.getDescription()));
            }
            if(!courseDao.update(course)){
                return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to update course");
            }else{
                return complete(StatusCodes.OK,"course update success");
            }
        });
    }
    private Route deleteCourse(long id){
        Course course = courseDao.findById(id);
        if(course==null){
            return complete(StatusCodes.NOT_FOUND,"course not found");
        }
        if(!courseDao.delete(course)){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to delete course");
        }
        return complete(StatusCodes.OK,"course deleted");
    }
}
