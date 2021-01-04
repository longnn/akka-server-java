package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Rejections;
import akka.http.javadsl.server.Route;
import com.icod.ilearning.data.dao.TeacherDao;
import com.icod.ilearning.data.model.Teacher;
import com.icod.ilearning.services.protocol.teacher.create.RequestCreateTeacher;
import com.icod.ilearning.services.protocol.teacher.list.RequestGetTeacherList;
import com.icod.ilearning.services.protocol.teacher.list.ResponseGetTeacherList;
import com.icod.ilearning.services.protocol.teacher.update.RequestUpdateTeacher;
import com.icod.ilearning.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TeacherRoute extends AllDirectives {
    final TeacherDao teacherDao;
    public TeacherRoute(){
        teacherDao = new TeacherDao();
    }
    public Route createRoute() {
        return  pathEnd(() ->
                get(()-> entity(Jackson.unmarshaller(RequestGetTeacherList.class), request-> getTeacher(request))).orElse(
                        post(()-> entity(Jackson.unmarshaller(RequestCreateTeacher.class), request-> createTeacher(request))))
        ).orElse(
                path(PathMatchers.longSegment(), id->
                        get(()-> getTeacher(id)).orElse(
                                put(()-> updateTeacher(id)).orElse(
                                        delete(()-> deleteTeacher(id))))
                ));
    }
    private Route getTeacher(RequestGetTeacherList request){
        CompletableFuture<ResponseGetTeacherList> future = CompletableFuture.supplyAsync(() -> {
            List<Teacher> teachers = teacherDao.getAll(request.getName());
            ResponseGetTeacherList response = new ResponseGetTeacherList();
            response.setTotal(teachers.size());
            response.setTeachers(teachers);
            response.setPerPage(request.getLimit());
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }
    private Route getTeacher(long id){
        Teacher teacher = teacherDao.findById(id);
        if(teacher==null){
            return complete(StatusCodes.NOT_FOUND,"teacher not found");
        }
        return complete(StatusCodes.OK,teacher,Jackson.marshaller());
    }
    private Route createTeacher(RequestCreateTeacher request) {
        // VALIDATE
        if(ValidationUtil.isNullOrEmpty(request.getFirstName())){
            return reject(Rejections.malformedFormField("title","title required"));
        }
        Teacher teacher = new Teacher();
        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setCreatedAt(new Date());
        teacher.setUpdatedAt(new Date());
        Long id = teacherDao.insert(teacher);
        if(id == null){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to create teacher");
        }else {
            return complete(StatusCodes.OK,"teacher create success");
        }
    }
    private Route updateTeacher(long id){
        Teacher teacher = teacherDao.findById(id);
        if(teacher==null){
            return complete(StatusCodes.NOT_FOUND,"teacher not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdateTeacher.class), request -> {
            if(!ValidationUtil.isNullOrEmpty(request.getFirstName())){
                teacher.setFirstName(request.getFirstName());
            }
            if(!ValidationUtil.isNullOrEmpty(request.getLastName())){
                teacher.setLastName(request.getLastName());
            }
            teacher.setStatus(request.getStatus());
            if(!teacherDao.update(teacher)){
                return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to update teacher");
            }else{
                return complete(StatusCodes.OK,"teacher update success");
            }
        });
    }
    private Route deleteTeacher(long id){
        Teacher teacher = teacherDao.findById(id);
        if(teacher==null){
            return complete(StatusCodes.NOT_FOUND,"teacher not found");
        }
        if(!teacherDao.delete(teacher)){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to delete teacher");
        }
        return complete(StatusCodes.OK,"teacher deleted");
    }
}
