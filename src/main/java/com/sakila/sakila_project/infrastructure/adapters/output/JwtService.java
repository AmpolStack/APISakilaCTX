package com.sakila.sakila_project.infrastructure.adapters.output;

import com.sakila.sakila_project.domain.ports.input.IJwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService implements IJwtService {

    public String GenerateToken(Map<String, String> claims, int expiration, String secret) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(GetSecretKey(secret))
                .compact();
    }

    public String GenerateToken(Map<String, String> claims, String subject, int expiration, String secret) {
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration))
                .signWith(GetSecretKey(secret))
                .compact();
    }

    //128 Bits length
    public String GenerateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public boolean IsTokenExpired(String token, String secret) {
        var claims = GetClaimsFromToken(token, secret);
        return claims.getExpiration().after(new Date());
    }

    public List<Object> GetClaimsList(String token, String secret, Map<String, Class<?>> map){
        var claims = GetClaimsFromToken(token, secret);
        var resultList = new ArrayList<>();

        map.forEach((k, v) ->{
            var valueClaim = claims.get(k);
            if(v.isInstance(valueClaim)){
                resultList.add(v);
            }
            else{
                throw new ClassCastException("Invalid claim");
            }
        });
        return resultList;
    }


    public <T> T GetClaim(String token, String secret, String claimName, Class<T> claimType){
        var claims = GetClaimsFromToken(token, secret);
        var claimValue = claims.get(claimName);
        if(claimType.isInstance(claimType)){
            return claimType.cast(claimValue);
        }
        else{
            throw new ClassCastException("Invalid claim");
        }
    }

    public Claims GetAllClaims(String token, String secret){
        return GetClaimsFromToken(token, secret);
    }

    private Claims GetClaimsFromToken(String token, String secret) {
        return Jwts
                .parser()
                .verifyWith(GetSecretKey(secret))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey GetSecretKey(String secret) {
        var keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
