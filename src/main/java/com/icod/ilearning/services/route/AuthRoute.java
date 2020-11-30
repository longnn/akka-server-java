package com.icod.ilearning.services.route;

import akka.actor.ActorSystem;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.MalformedFormFieldRejection;
import akka.http.javadsl.server.Rejections;
import akka.http.javadsl.server.Route;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.icod.ilearning.Application;
import com.icod.ilearning.data.dao.RefreshTokenDao;
import com.icod.ilearning.data.dao.UserDao;
import com.icod.ilearning.data.model.RefreshTokenModel;
import com.icod.ilearning.data.model.UserModel;
import com.icod.ilearning.data.object.JwtToken;
import com.icod.ilearning.services.protocol.auth.login.RequestLogin;
import com.icod.ilearning.services.protocol.auth.login.ResponseLogin;
import com.icod.ilearning.services.protocol.auth.refreshToken.RequestRefreshToken;
import com.icod.ilearning.services.protocol.auth.register.RequestRegister;
import com.icod.ilearning.util.Language;
import com.icod.ilearning.util.SecurityUtil;
import com.icod.ilearning.util.ValidationUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class AuthRoute extends AllDirectives {

    final UserDao userDao = new UserDao();
    final Config config = ConfigFactory.load();
    final Language lang = Language.getInstance();
    ;

    final ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    public AuthRoute() {
    }

    public Route createRoute() {
        return  path("login", () -> post(() -> entity(Jackson.unmarshaller(RequestLogin.class), request -> login(request)))).orElse(
                path("register", () -> post(() -> entity(Jackson.unmarshaller(RequestRegister.class), request -> register(request))))).orElse(
                path("refreshToken", () -> post(() -> entity(Jackson.unmarshaller(RequestRefreshToken.class), request -> refreshToken(request)))));
    }

    private Route login(RequestLogin request) {
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            return reject(Rejections.malformedFormField("email", lang.trans("invalid_email")));
        }
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            return reject(Rejections.malformedFormField("password", lang.trans("invalid_password")));
        }
        UserModel user = userDao.findUserLogin(request.getEmail(), request.getPassword());
        if (user == null) {
            return complete(StatusCodes.UNAUTHORIZED, lang.trans("invalid_account"));
        }
        String jwtId = Application.idGen.newId();
        String secretKey = config.getString("security.jwt.secret");
        String issuer = config.getString("security.jwt.issuer");
        String subject = config.getString("security.jwt.subject");
        long ttl = config.getLong("security.jwt.ttl");
        long refreshTtl = config.getLong("security.jwt.refreshTtl");

        long nowMillis = new Date().getTime();
        long jwtExpMillis = nowMillis + ttl;
        long refExpMillis = nowMillis + refreshTtl;
        Date jwtExpDate = new Date(jwtExpMillis);
        Date refreshExpDate = new Date(refExpMillis);

        /* GENERATE JWT TOKEN */
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder()
                .setId(jwtId)
                .setIssuedAt(new Date())
                .setSubject(subject)
                .setIssuer(issuer)
                .claim("userId", user.getId())
                .signWith(signingKey);
        builder.setExpiration(jwtExpDate);
        String jwt = builder.compact();

        // REFRESH TOKEN
        RefreshTokenDao refreshTokenDao = new RefreshTokenDao();
        String refresh = SecurityUtil.generateRefreshToken(user.getId(), secretKey);
        RefreshTokenModel refreshToken = new RefreshTokenDao().findByUserId(user.getId());
        if (refreshToken == null) {
            refreshToken = new RefreshTokenModel();
            refreshToken.setUserId(user.getId());
            refreshToken.setCreatedAt(new Date());
            refreshToken.setExpiredAt(refreshExpDate);
            refreshToken.setToken(refresh);
            refreshTokenDao.insert(refreshToken);
        } else {
            if (refreshToken.getExpiredAt().compareTo(new Date()) < 0) {
                refreshToken.setToken(refresh);
                refreshToken.setExpiredAt(refreshExpDate);
                refreshTokenDao.update(refreshToken);
            }
        }

        // Response
        ResponseLogin response = new ResponseLogin();
        response.setAccessToken(jwt);
        response.setExpiresIn(jwtExpDate);
        response.setRefreshToken(refreshToken.getToken());
        return complete(StatusCodes.OK, response, Jackson.marshaller(objectMapper));
    }

    private Route register(RequestRegister request) {
        /* VALIDATION */
        if (ValidationUtil.isNullOrEmpty(request.getName())) {
            return reject(Rejections.malformedFormField("name", lang.trans("required_name")));
        }
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            return reject(Rejections.malformedFormField("email", lang.trans("invalid_email")));
        }
        if (!ValidationUtil.isValidPassword(request.getPassword())) {
            return reject(Rejections.malformedFormField("password", lang.trans("invalid_password")));
        }
        UserModel user = new UserModel();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(SecurityUtil.md5(request.getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setStatus(1);
        return complete("LOGIN");
    }

    private Route refreshToken(RequestRefreshToken request) {
        if(ValidationUtil.isNullOrEmpty(request.getRefreshToken())){
            return reject(Rejections.malformedFormField("refreshToken","refreshToken is required"));
        }
        RefreshTokenDao refreshTokenDao = new RefreshTokenDao();
        RefreshTokenModel refreshToken = refreshTokenDao.find(request.getRefreshToken());
        if(refreshToken==null || refreshToken.getExpiredAt().compareTo(new Date()) < 0){
            return complete(StatusCodes.FORBIDDEN,"invalid refreshToken");
        }
        // GENERATE NEW JWT
        String jwtId = Application.idGen.newId();
        String secretKey = config.getString("security.jwt.secret");
        String issuer = config.getString("security.jwt.issuer");
        String subject = config.getString("security.jwt.subject");
        long ttl = config.getLong("security.jwt.ttl");

        long nowMillis = new Date().getTime();
        long jwtExpMillis = nowMillis + ttl;
        Date jwtExpDate = new Date(jwtExpMillis);

        /* GENERATE JWT TOKEN */
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        JwtBuilder builder = Jwts.builder()
                .setId(jwtId)
                .setIssuedAt(new Date())
                .setSubject(subject)
                .setIssuer(issuer)
                .claim("userId", refreshToken.getUserId())
                .signWith(signingKey);
        builder.setExpiration(jwtExpDate);
        String jwt = builder.compact();

        // Response
        ResponseLogin response = new ResponseLogin();
        response.setAccessToken(jwt);
        response.setExpiresIn(jwtExpDate);
        response.setRefreshToken(refreshToken.getToken());
        //return complete(StatusCodes.FORBIDDEN,"refreshToken failed");
        return complete(StatusCodes.OK, response, Jackson.marshaller(objectMapper));
    }
}
