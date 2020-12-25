package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Rejections;
import akka.http.javadsl.server.Route;
import com.icod.ilearning.data.dao.RoleDao;
import com.icod.ilearning.data.model.RoleModel;
import com.icod.ilearning.services.protocol.role.create.RequestCreateRole;
import com.icod.ilearning.services.protocol.role.list.RequestGetRoleList;
import com.icod.ilearning.services.protocol.role.list.ResponseGetRoleList;
import com.icod.ilearning.services.protocol.role.update.RequestUpdateRole;
import com.icod.ilearning.util.ValidationUtil;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RoleRoute extends AllDirectives {

    final RoleDao roleDao;

    public RoleRoute() {
        roleDao = new RoleDao();
    }

    public Route createRoute() {
        return pathEnd(() ->
                    get(() -> entity(Jackson.unmarshaller(RequestGetRoleList.class), request -> getRole(request))).orElse(
                    post(() -> entity(Jackson.unmarshaller(RequestCreateRole.class), request -> createRole(request))))
                ).orElse(
                path(PathMatchers.longSegment(), id ->
                    get(() -> getRole(id)).orElse(
                    put(() -> updateRole(id)).orElse(
                    delete(() -> deleteRole(id))))
                ).orElse(
                    path("find",() -> post(()-> getAllRole()))
                ));
    }

    private Route getAllRole() {
        CompletableFuture<ResponseGetRoleList> future = CompletableFuture.supplyAsync(() -> {
            List<RoleModel> roles = roleDao.getAll(null,null,null);
            ResponseGetRoleList response = new ResponseGetRoleList();
            response.setTotal(roles.size());
            response.setPerPage(roles.size());
            response.setRoles(roles);
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }

    private Route getRole(RequestGetRoleList request) {
        CompletableFuture<ResponseGetRoleList> future = CompletableFuture.supplyAsync(() -> {
            List<RoleModel> roles = roleDao.getAll(request.getName(), request.getLimit(), request.getOffset());
            ResponseGetRoleList response = new ResponseGetRoleList();
            response.setTotal(roles.size());
            response.setPerPage(request.getLimit() != null ? request.getLimit() : roles.size());
            response.setRoles(roles);
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }

    private Route getRole(long id) {
        RoleModel role = roleDao.findById(id);
        if (role == null) {
            return complete(StatusCodes.NOT_FOUND, "role not found");
        }
        return complete(StatusCodes.OK, role, Jackson.marshaller());
    }

    private Route createRole(RequestCreateRole request) {
        if (ValidationUtil.isNullOrEmpty(request.getName())) {
            return reject(Rejections.malformedFormField("title","title required"));
        }
        RoleModel role = new RoleModel();
        role.setName(request.getName());
        role.setStatus(request.getStatus());
        role.setCreatedAt(new Date());
        role.setUpdatedAt(new Date());
        Long id = roleDao.insert(role);
        if (id == null) {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to create role");
        } else {
            return complete(StatusCodes.OK, "role create success");
        }
    }

    private Route updateRole(long id) {
        RoleModel role = roleDao.findById(id);
        if (role == null) {
            return complete(StatusCodes.NOT_FOUND, "role not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdateRole.class), request -> {
            if (!ValidationUtil.isNullOrEmpty(request.getName())) {
                role.setName(request.getName());
            }
            role.setStatus(request.getStatus());
            if (!roleDao.update(role)) {
                return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to update role");
            } else {
                return complete(StatusCodes.OK, "role update success");
            }
        });
    }

    private Route deleteRole(long id) {
        RoleModel role = roleDao.findById(id);
        if (role == null) {
            return complete(StatusCodes.NOT_FOUND, "role not found");
        }
        if (!roleDao.delete(role)) {
            return complete(StatusCodes.INTERNAL_SERVER_ERROR, "fail to delete role");
        }
        return complete(StatusCodes.OK, "role deleted");
    }
}
