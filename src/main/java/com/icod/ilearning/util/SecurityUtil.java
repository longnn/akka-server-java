package com.icod.ilearning.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class SecurityUtil {

    public static String md5(String input){
        String md5Hex = DigestUtils.md5Hex(input);
        return md5Hex;
    }

    public static String generateJWT(String id, String secretKey, String subject, String issuer, Date expireDate){
        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(new Date())
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey);

        builder.setExpiration(expireDate);

        // Generate refresh token
        //Builds the JWT and serializes it to a compact, URL-safe string
        String jwt = builder.compact();
        return jwt;
    }

    public static String generateRefreshToken(long userId,String secretKey){
        String valueToEncrypt = secretKey+"_"+userId+"_"+ UUID.randomUUID();
        return DigestUtils.sha256Hex(valueToEncrypt);
    }

    public static Claims decodeJWT(String jwt, String secretKey) throws UnsupportedJwtException,MalformedJwtException, SignatureException, IllegalArgumentException, ExpiredJwtException
    {
        Claims claims = Jwts.parserBuilder().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).build().parseClaimsJws(jwt).getBody();
        return claims;
    }
}

