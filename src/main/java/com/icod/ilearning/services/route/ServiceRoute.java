package com.icod.ilearning.services.route;

import akka.actor.ActorSystem;
import akka.http.javadsl.model.HttpHeader;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.*;
import com.icod.ilearning.util.SecurityUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.jsonwebtoken.Claims;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ServiceRoute extends AllDirectives {
    final ActorSystem actorSystem;
    final Config config = ConfigFactory.load();
    final List<HttpHeader> headers = new ArrayList<>();

    public ServiceRoute(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        this.headers.add(HttpHeader.parse("Access-Control-Allow-Origin","*"));
        this.headers.add(HttpHeader.parse("Access-Control-Allow-Headers","*"));
    }

    private RejectionHandler rejectionHandler = RejectionHandler.newBuilder().handle(MalformedFormFieldRejection.class, rejections -> {
        return complete(StatusCodes.BAD_REQUEST, rejections.errorMsg());
    }).build();
    private ExceptionHandler exceptionHandler = ExceptionHandler.newBuilder().matchAny(throwable -> {
        throwable.printStackTrace();
        return complete(StatusCodes.INTERNAL_SERVER_ERROR, "Exception:" + throwable.getMessage());
    }).build();


    public Route create() {
        return handleExceptions(exceptionHandler, () ->
                handleRejections(rejectionHandler, () ->
                        respondWithHeaders(headers,()->
                        pathPrefix("api", () ->
                                pathPrefix("auth", () -> new AuthRoute().createRoute()).orElse(
                                pathPrefix("users", () -> new UserRoute().createRoute())).orElse(
                                pathPrefix("roles", () -> new RoleRoute().createRoute())).orElse(
                                pathPrefix("permissions", () -> new PermissionRoute().createRoute())).orElse(
                                pathPrefix("courses", () -> new CourseRoute().createRoute())).orElse(
                                pathPrefix("teachers", () -> new TeacherRoute().createRoute()))
                        ))));
    }

    private Route authorize(Supplier<Route> inner){
        return optionalHeaderValueByName("Authorization",opHeader -> {
            if(!opHeader.isPresent()){
                return complete(StatusCodes.UNAUTHORIZED,"unauthorized");
            }
            String secretKey = config.getString("security.jwt.secret");
            String jwt = opHeader.get().replace("Bearer ","");
            Claims claims = SecurityUtil.decodeJWT(jwt,secretKey);
            return complete(jwt);
        });
    }
}
