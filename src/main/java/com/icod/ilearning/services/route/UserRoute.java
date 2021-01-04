package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icod.ilearning.data.dao.RoleDao;
import com.icod.ilearning.data.dao.UserDao;
import com.icod.ilearning.data.model.Role;
import com.icod.ilearning.data.model.User;
import com.icod.ilearning.services.protocol.user.changeStatus.RequestChangeUserStatus;
import com.icod.ilearning.services.protocol.user.checkEmail.RequestCheckEmail;
import com.icod.ilearning.services.protocol.user.checkEmail.ResponseCheckEmail;
import com.icod.ilearning.services.protocol.user.create.RequestCreateUser;
import com.icod.ilearning.services.protocol.user.create.ResponseCreateUser;
import com.icod.ilearning.services.protocol.user.deleteUsers.RequestDeleteUsers;
import com.icod.ilearning.services.protocol.user.find.RequestFindUsers;
import com.icod.ilearning.services.protocol.user.find.ResponseGetUserList;
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
    final Config config = ConfigFactory.load();
    final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public UserRoute() {
    }

    /*
    POST   : /users -> CREATE USER
    POST   : /users/me -> FIND LOGGED IN USER
    POST   : /users/find -> FIND USERS
    POST   : /users/checkEmail -> IS UNIQUE EMAIL
    GET    : /users/{id} -> FETCH ID
    PUT    : /users/{id} -> UPDATE USER
    PUT    : /users/updateStatus -> CHANGE USER STATUS
    DELETE : /users/{id} -> DELETE USER
    DELETE : /users/deleteItems -> DELETE USERS
    */
    public Route createRoute() {
        return pathEnd(() -> post(() -> createUser())).orElse(
                path(PathMatchers.longSegment(), id ->
                    pathEnd(() ->
                        get(() -> getUser(id)).orElse(
                        put(() -> updateUser(id)).orElse(
                        delete(() -> deleteUser(id)))))
                ))
                .orElse(path("me", () -> getUserByToken()))
                .orElse(path("find", () -> post(() -> find()))
                .orElse(path("checkEmail", () -> post(() -> checkEmail())))
                .orElse(path("deleteItems", () -> put(() -> deleteUsers())))
                .orElse(path("updateStatus", () -> put(() -> updateStatus())))
                );
    }

    private Route checkEmail() {
        return entity(Jackson.unmarshaller(RequestCheckEmail.class), request -> {
            boolean isExisted = userDao.isEmailExists(request.getEmail());
            ResponseCheckEmail response = new ResponseCheckEmail(isExisted);
            return completeOK(response, Jackson.marshaller());
        });
    }

    private Route find() {
        return entity(Jackson.unmarshaller(objectMapper, RequestFindUsers.class), request -> {
            CompletableFuture<ResponseGetUserList> future = CompletableFuture.supplyAsync(() -> {
                List<User> users = userDao.getAll(null);
                ResponseGetUserList response = new ResponseGetUserList();
                response.setTotal(users.size());
                response.setUsers(users);
                return response;
            });
            return completeOKWithFuture(future, Jackson.marshaller());
        });
    }

    private Route getUserByToken() {
        return optionalHeaderValueByName("Authorization", opHeader -> {
            if (!opHeader.isPresent()) {
                return complete(StatusCodes.UNAUTHORIZED, "unauthorized");
            }
            String secretKey = config.getString("security.jwt.secret");
            String jwt = opHeader.get().replace("Bearer ", "");
            try {
                Claims claims = SecurityUtil.decodeJWT(jwt, secretKey);
                long userId = claims.get("userId", Long.class);
                UserDao userDao = new UserDao();
                User user = userDao.findById(userId);
                if (user == null) {
                    return complete(StatusCodes.UNAUTHORIZED, "unauthorized");
                }
                return complete(StatusCodes.OK, user, Jackson.marshaller());
            } catch (Exception e) {
                return complete(StatusCodes.UNAUTHORIZED, "invalid or expired token");
            }
        });
    }

    private Route getUser(long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        return complete(StatusCodes.OK, user, Jackson.marshaller());
    }

    private Route createUser() {
        return entity(Jackson.unmarshaller(RequestCreateUser.class), request -> {
            ResponseCreateUser response;
            // VALIDATE
            if (ValidationUtil.isNullOrEmpty(request.getName())) {
                return reject(Rejections.malformedFormField("name", "name required"));
            }
            if (!ValidationUtil.isValidEmail(request.getEmail())) {
                return reject(Rejections.malformedFormField("email", "invalid email address"));
            }
            if (userDao.isEmailExists(request.getEmail())) {
                return reject(Rejections.malformedFormField("email", "email has already existed"));
            }
            if (!ValidationUtil.isValidPassword(request.getPassword())) {
                return reject(Rejections.malformedFormField("password", "password required and must contain at lease 8 characters"));
            }
            if (!ValidationUtil.isNumber(request.getRoleId())) {
                return reject(Rejections.malformedFormField("role", "role required"));
            }
            RoleDao roleDao = new RoleDao();
            Role role = roleDao.findById(Integer.parseInt(request.getRoleId()));

            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(SecurityUtil.md5(request.getPassword()));
            user.setCreatedAt(new Date());
            user.setUpdatedAt(new Date());
            user.setStatus(Integer.parseInt(request.getStatus()));
            user.setRole(role);
            Long id = userDao.create(user);
            if (id == null) {
                return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to create user");
            } else {
                return complete(StatusCodes.OK, "user create success");
            }
        });
    }

    private Route updateUser(long id) {
        User user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdateUser.class), request -> {
            if (!ValidationUtil.isNullOrEmpty(request.getFullname())) {
                user.setName(request.getFullname());
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
        User user = userDao.findById(id);
        if (user == null) {
            return complete(StatusCodes.NOT_FOUND, "user not found");
        }
        if (!userDao.delete(user)) {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to delete user");
        }
        return complete(StatusCodes.OK, "user deleted");
    }

    private Route deleteUsers() {
        return entity(Jackson.unmarshaller(objectMapper, RequestDeleteUsers.class), request -> {
            if (userDao.deleteItems(request.getIds())) {
                return complete(StatusCodes.OK, "users deleted");
            } else {
                return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to delete users");
            }
        });
    }

    private Route updateStatus() {
        return entity(Jackson.unmarshaller(objectMapper, RequestChangeUserStatus.class), request -> {
            int status = Integer.parseInt(request.getStatus());
           if(userDao.changeStatus(request.getIds(), status)) {
               return complete(StatusCodes.OK, "users status updated");
           } else {
               return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to update users status");
           }
        });
    }
}
