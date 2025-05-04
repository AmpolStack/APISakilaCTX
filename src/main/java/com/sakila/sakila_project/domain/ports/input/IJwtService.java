package com.sakila.sakila_project.domain.ports.input;

import io.jsonwebtoken.Claims;

import java.util.*;

public interface IJwtService {
    String GenerateToken(Map<String, String> claims, int expiration, String secret);
    String GenerateToken(Map<String, String> claims, String subject, int expiration, String secret);
    String GenerateRefreshToken();
    boolean IsTokenExpired(String token, String secret);
    List<Object> GetClaimsList(String token, String secret, Map<String, Class<?>> map);
    <T> T GetClaim(String token, String secret, String claimName, Class<T> claimType);
    Claims GetAllClaims(String token, String secret);
}
