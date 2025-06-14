package com.sakila.sakila_project.infrastructure.adapters.output;

import com.sakila.sakila_project.domain.ports.output.IJwtService;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService implements IJwtService {

    public Result<String> GenerateToken(Map<String, String> claims, int expiration, String secret) {

        var errors = ProveMany(expiration, secret);
        if(!errors.isEmpty()){
            return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));
        }

        var builder = Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration));

        try{
            builder.signWith(GetSecretKey(secret));
        }
        catch (WeakKeyException ex){
            return Result.Failed(new Error("This key is invalid", ErrorType.OPERATION_ERROR));
        }

        return Result.Success(builder.compact());
    }

    public Result<String> GenerateToken(Map<String, String> claims, String subject, int expiration, String secret) {
        var builder = Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expiration));

        try{
            builder.signWith(GetSecretKey(secret));
        }
        catch (WeakKeyException ex){
            return Result.Failed(new Error("This key is invalid", ErrorType.PROCESSING_ERROR));
        }

        return Result.Success(builder.compact());
    }

    //128 Bits length
    public String GenerateRefreshToken(){
        return UUID.randomUUID().toString();
    }

    public Result<Void> IsTokenExpired(String token, String secret) {
        var claims = GetClaimsFromToken(token, secret);
        if(!claims.isSuccess()){
            return Result.Failed(claims.getError());
        }

        if(!claims.getData().getExpiration().after(new Date())){
            return Result.Failed(new Error("This token is expired", ErrorType.FAILURE_ERROR));
        }
        else{
            return Result.Success();
        }
    }

    public Result<List<Object>> GetClaimsList(String token, String secret, Map<String, Class<?>> map){
        var claims = GetClaimsFromToken(token, secret);

        if(!claims.isSuccess()){
            return Result.Failed(claims.getError());
        }

        var valueClaims = claims.getData();
        var resultList = new ArrayList<>();

        for(var entry : map.entrySet()){
            var valueClaim = valueClaims.get(entry.getKey());

            if(entry.getValue().isInstance(valueClaim)){
                resultList.add(entry.getValue());
            }
            else{
                return Result.Failed(new Error("One claims are invalid to parse", ErrorType.OPERATION_ERROR));
            }

        }

        return Result.Success(resultList);
    }


    public <T> Result<T> GetClaim(String token, String secret, String claimName, Class<T> claimType){

        if(claimName.isBlank()){
            return Result.Failed(new Error("claim name is blank", ErrorType.VALIDATION_ERROR));
        }

        var claims = GetClaimsFromToken(token, secret);

        if(!claims.isSuccess()){
            return Result.Failed(claims.getError());
        }

        var claimValue = claims.getData().get(claimName);
        if(claimType.isInstance(claimType)){
            var resp = claimType.cast(claimValue);
            return Result.Success(resp);
        }
        else{
            return Result.Failed(new Error("Invalid cast", ErrorType.OPERATION_ERROR));
        }
    }

    public Result<Claims> GetAllClaims(String token, String secret){
        return GetClaimsFromToken(token, secret);
    }

    private Result<Claims> GetClaimsFromToken(String token, String secret) {
        if(token.isBlank()){
            return Result.Failed(new Error("Token is blank", ErrorType.VALIDATION_ERROR));
        }
        if(secret.isBlank()){
            return Result.Failed(new Error("Secret is blank", ErrorType.VALIDATION_ERROR));
        }

        var parser = Jwts
                .parser()
                .verifyWith(GetSecretKey(secret))
                .build();
        try{
            var resp = parser.parseSignedClaims(token).getPayload();
            return Result.Success(resp);
        }
        catch (JwtException | IllegalArgumentException ex){
            return Result.Failed(new Error("Invalid token", ErrorType.OPERATION_ERROR));
        }
    }

    private SecretKey GetSecretKey(String secret) {
        var keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private List<String> ProveMany(int expiration, String secret){
        var errors = new ArrayList<String>();

        if(expiration < 0){
            errors.add("The expiration must be greater than 0");
        }
        if(secret.isBlank()){
            errors.add("The secret is mandatory");
        }
        return errors;
    }
}
