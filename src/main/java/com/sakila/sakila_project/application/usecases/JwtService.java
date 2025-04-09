package com.sakila.sakila_project.application.usecases;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {

    @Value("${spring.security.jwt.token.expirationMs}")
    private int tokenExpiration;

    @Value("${spring.security.jwt.refresh.token.expirationMs}")
    private int refreshTokenExpiration;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    private SecretKey getSecretKey() {
        var keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //TODO: include logic of save tokens registry in database

    public String generateToken(Map<String, String> claims, String subject) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + this.tokenExpiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //128 Bits length
    public String generateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public boolean isTokenValid(String token) {
        var claims = getClaimsFromToken(token);
        return claims.getExpiration().after(new Date());
    }

    public List<Object> getClaimsList(String token, Map<String, Class<?>> map){
        var claims = getClaimsFromToken(token);
        var resultList = new ArrayList<>();

        map.forEach((k, v) ->{
            var valueClaim = claims.get(k);
            if(v.isInstance(valueClaim)){
                resultList.add(v);
            }
            else{
                throw new ClassCastException("Invalid claim");
                //resultList.add(null);
            }
        });
        return resultList;
    }

    public <T> T getClaim(String token, String claimName, Class<T> claimType){
        var claims = getClaimsFromToken(token);
        var claimValue = claims.get(claimName);
        if(claimType.isInstance(claimType)){
            return claimType.cast(claimValue);
        }
        else{
            throw new ClassCastException("Invalid claim");
            //return null;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
