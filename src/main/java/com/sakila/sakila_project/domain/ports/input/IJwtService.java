package com.sakila.sakila_project.domain.ports.input;

import com.sakila.sakila_project.domain.results.Result;
import io.jsonwebtoken.Claims;

import java.util.*;

public interface IJwtService {
    Result<String> GenerateToken(Map<String, String> claims, int expiration, String secret);
    Result<String> GenerateToken(Map<String, String> claims, String subject, int expiration, String secret);
    String GenerateRefreshToken();
    Result<Void> IsTokenExpired(String token, String secret);
    Result<List<Object>> GetClaimsList(String token, String secret, Map<String, Class<?>> map);
    <T> Result<T> GetClaim(String token, String secret, String claimName, Class<T> claimType);
    Result<Claims> GetAllClaims(String token, String secret);
}
