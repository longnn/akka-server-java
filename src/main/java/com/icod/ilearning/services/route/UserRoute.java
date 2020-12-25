package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.*;
import com.icod.ilearning.data.dao.PermissionDao;
import com.icod.ilearning.data.dao.RoleDao;
import com.icod.ilearning.data.dao.UserDao;
import com.icod.ilearning.data.model.PermissionModel;
import com.icod.ilearning.data.model.RoleModel;
import com.icod.ilearning.data.model.UserModel;
import com.icod.ilearning.services.protocol.user.assignPermission.RequestAssignUserPermission;
import com.icod.ilearning.services.protocol.user.assignRole.RequestAssignUserRole;
import com.icod.ilearning.services.protocol.user.checkEmail.RequestCheckEmail;
import com.icod.ilearning.services.protocol.user.checkEmail.ResponseCheckEmail;
import com.icod.ilearning.services.protocol.user.create.RequestCreateUser;
import com.icod.ilearning.services.protocol.user.create.ResponseCreateUser;
import com.icod.ilearning.services.protocol.user.list.RequestGetUserList;
import com.icod.ilearning.services.protocol.user.list.ResponseGetUserList;
import com.icod.ilearning.services.protocol.user.update.RequestUpdateUser;
import com.icod.ilearning.util.SecurityUtil;
import com.icod.ilearning.util.ValidationUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserRoute extends AllDirectives {

    final UserDao userDao = new UserDao();
    final RoleDao roleDao = new RoleDao();
    final Config config = ConfigFactory.load();
    final PermissionDao permissionDao = new PermissionDao();

    public UserRoute() {
    }

    public Route createRoute() {
        return pathEnd(() ->
                get(() -> entity(Jackson.unmarshaller(RequestGetUserList.class), request -> getUsers(request))).orElse(
                    post(() -> entity(Jackson.unmarshaller(RequestCreateUser.class), request -> createUser(request))))
                ).orElse(
                path(PathMatchers.longSegment(), id ->
                    pathEnd(() ->
                        get(() -> getUser(id)).orElse(
                        put(() -> updateUser(id)).orElse(
                        delete(() -> deleteUser(id))))
                    )
                )).orElse(
                    path(PathMatchers.longSegment().slash("assignRole"), id -> post(() -> entity(Jackson.unmarshaller(RequestAssignUserRole.class), request -> assignRole(id, request)))
                )).orElse(
                    path(PathMatchers.longSegment().slash("assignPermission"), id -> post(() -> entity(Jackson.unmarshaller(RequestAssignUserPermission.class), request -> assignPermission(id, request)))
                )).orElse(
                    path("me",()-> getUserByToken()).orElse(
                    path("find",() -> find())).orElse(
                    path("checkEmail",() -> post(()-> entity(Jackson.unmarshaller(RequestCheckEmail.class), request-> checkEmail(request)))))
                );
    }

    private Route checkEmail(RequestCheckEmail request){
        UserDao userDao = new UserDao();
        boolean isExisted = userDao.isEmailExists(request.getEmail());
        ResponseCheckEmail response =  new ResponseCheckEmail();
        response.setExisted(isExisted);
        return complete(StatusCodes.OK,response,Jackson.marshaller());
    }

    private Route find(){
        CompletableFuture<ResponseGetUserList> future = CompletableFuture.supplyAsync(() -> {
            List<UserModel> users = userDao.getAll(null);
            ResponseGetUserList response = new ResponseGetUserList();
            response.setTotal(users.size());
            response.setUsers(users);
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }

    private Route getUsers(RequestGetUserList request) {
        CompletableFuture<ResponseGetUserList> future = CompletableFuture.supplyAsync(() -> {
            List<UserModel> users = userDao.getAll(request.getName());
            ResponseGetUserList response = new ResponseGetUserList();
            response.setTotal(users.size());
            response.setUsers(users);
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }

    private Route getUserByToken(){
        return optionalHeaderValueByName("Authorization",opHeader -> {
            if(!opHeader.isPresent()){
                return complete(StatusCodes.UNAUTHORIZED,"unauthorized");
            }
            String secretKey = config.getString("security.jwt.secret");
            String jwt = opHeader.get().replace("Bearer ","");
            try {
                Claims claims = SecurityUtil.decodeJWT(jwt, secretKey);
                long userId = claims.get("userId",Long.class);
                UserDao userDao = new UserDao();
                UserModel user = userDao.findById(userId);
                if(user == null){
                    return complete(StatusCodes.UNAUTHORIZED,"unauthorized");
                }
                return complete(StatusCodes.OK,user,Jackson.marshaller());
            }catch (Exception e) {
                return complete(StatusCodes.UNAUTHORIZED, "invalid or expired token");
            }
        });
    }

    private Route getUser(long id) {
        UserModel user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        return complete(StatusCodes.OK, user, Jackson.marshaller());
    }
    
    private Route createUser(RequestCreateUser request) {
        ResponseCreateUser response;
        // VALIDATE
        if (ValidationUtil.isNullOrEmpty(request.getName())) {
            return reject(Rejections.malformedFormField("name","name required"));
        }
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            return reject(Rejections.malformedFormField("email","invalid email address"));
        }
        if (userDao.isEmailExists(request.getEmail())) {
            return reject(Rejections.malformedFormField("email","email has already existed"));
        }
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            return reject(Rejections.malformedFormField("password","password required and must contain at lease 8 characters"));
        }
        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(SecurityUtil.md5(request.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus(request.getStatus());
        Long id = userDao.insert(user);
        if (id == null) {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to delete user");
        } else {
            return complete(StatusCodes.OK, "user create success");
        }
    }

    private Route updateUser(long id) {
        UserModel user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdateUser.class), request -> {
            if (!ValidationUtil.isNullOrEmpty(request.getName())) {
                user.setName(request.getName());
            }
            if (ValidationUtil.isValidPassword(request.getPassword())) {
                user.setPassword(SecurityUtil.md5(request.getPassword()));
            }
            if (!userDao.update(user)) {
                return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to update user");
            } else {
                return complete(StatusCodes.OK, "user update success");
            }
        });
    }

    private Route deleteUser(long id) {
        UserModel user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        if (!userDao.delete(user)) {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to delete user");
        }
        return complete(StatusCodes.OK, "user deleted");
    }

    private Route assignRole(long id, RequestAssignUserRole request) {
        UserModel user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        if (request.getRoleIds().length == 0) {
            return complete(StatusCodes.BAD_REQUEST, "roles required");
        }
        List<RoleModel> roles = roleDao.getRoleByIds(request.getRoleIds());
        if (userDao.update(user)) {
            return complete(StatusCodes.OK, "set user role success");
        } else {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "set user role fail");
        }
    }

    private Route assignPermission(long id, RequestAssignUserPermission request) {
        UserModel user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        if (request.getPermissionIds().length == 0) {
            return complete(StatusCodes.BAD_REQUEST, "permissions required");
        }
        List<PermissionModel> permissions = permissionDao.getByIds(request.getPermissionIds());
        if (userDao.update(user)) {
            return complete(StatusCodes.OK, "set user permissions success");
        } else {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "set user permissions fail");
        }
    }
}
