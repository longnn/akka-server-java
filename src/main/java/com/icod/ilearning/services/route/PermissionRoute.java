package com.icod.ilearning.services.route;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Rejections;
import akka.http.javadsl.server.Route;
import com.icod.ilearning.data.dao.PermissionDao;
import com.icod.ilearning.data.model.Permission;
import com.icod.ilearning.services.protocol.permission.create.RequestCreatePermission;
import com.icod.ilearning.services.protocol.permission.list.ResponseGetPermissionList;
import com.icod.ilearning.services.protocol.permission.update.RequestUpdatePermission;
import com.icod.ilearning.util.ValidationUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PermissionRoute extends AllDirectives {

    final PermissionDao permissionDao;
    public PermissionRoute(){
        permissionDao = new PermissionDao();
    }

    public Route createRoute() {
        return  pathEnd(() ->
                get(() -> parameterMap(paramMap -> getPermission(paramMap))).orElse(
                post(()-> entity(Jackson.unmarshaller(RequestCreatePermission.class), request-> createPermission(request))))
        ).orElse(
                path(PathMatchers.longSegment(), id->
                        get(()-> getPermission(id)).orElse(
                        put(()-> updatePermission(id)).orElse(
                        delete(()-> deletePermission(id))))
                ));
    }

    private Route getPermission(Map<String,String> request){
        CompletableFuture<ResponseGetPermissionList> future = CompletableFuture.supplyAsync(() -> {
            Integer limit = null;
            Integer offset = null;
            String name = null;
            if (request.containsKey("name")) name = request.get("name");
            if (request.containsKey("limit") && NumberUtils.isParsable(request.get("limit"))) {
                limit = Integer.parseInt(request.get("limit"));
            }
            if (request.containsKey("offset") && NumberUtils.isParsable(request.get("offset"))) {
                offset = Integer.parseInt(request.get("offset"));
            }
            List<Permission> permissions = permissionDao.getAll(name, limit, offset);
            ResponseGetPermissionList response = new ResponseGetPermissionList();
            response.setTotal(permissions.size());
            response.setPermissions(permissions);
            return response;
        });
        return completeOKWithFuture(future, Jackson.marshaller());
    }

    private Route getPermission(long id){
        Permission permission = permissionDao.findById(id);
        if(permission==null){
            return complete(StatusCodes.NOT_FOUND,"permission not found");
        }
        return complete(StatusCodes.OK,permission,Jackson.marshaller());
    }

    private Route createPermission(RequestCreatePermission request) {
        // VALIDATE
        if(ValidationUtil.isNullOrEmpty(request.getName())){
            return reject(Rejections.malformedFormField("title","title required"));
        }
        Permission permission = new Permission();
        permission.setName(request.getName());
        permission.setStatus(request.getStatus());
        permission.setCreatedAt(new Date());
        permission.setUpdatedAt(new Date());
        Long id = permissionDao.insert(permission);
        if(id == null){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to create permission");
        }else {
            return complete(StatusCodes.OK,"permission create success");
        }
    }

    private Route updatePermission(long id){
        Permission permission = permissionDao.findById(id);
        if(permission==null){
            return complete(StatusCodes.NOT_FOUND,"permission not found");
        }
        return entity(Jackson.unmarshaller(RequestUpdatePermission.class), request -> {
            if(!ValidationUtil.isNullOrEmpty(request.getName())){
                permission.setName(request.getName());
            }
            permission.setStatus(request.getStatus());
            if(!permissionDao.update(permission)){
                return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to update permission");
            }else{
                return complete(StatusCodes.OK,"permission update success");
            }
        });
    }

    private Route deletePermission(long id){
        Permission permission = permissionDao.findById(id);
        if(permission==null){
            return complete(StatusCodes.NOT_FOUND,"permission not found");
        }
        if(!permissionDao.delete(permission)){
            return complete(StatusCodes.INTERNAL_SERVER_ERROR,"fail to delete permission");
        }
        return complete(StatusCodes.OK,"permission deleted");
    }
}
