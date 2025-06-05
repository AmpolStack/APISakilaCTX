package com.sakila.sakila_project.infrastructure.utils;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.domain.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public class ResponseHandler {

    public static <T> ResponseEntity<?> SendResponse(Supplier<Result<T>> supplier) {
        return SendResponse(supplier, HttpStatus.OK);
    }

    public static <T> ResponseEntity<?> SendResponse(Supplier<Result<T>> supplier, HttpStatusCode statusCode) {
        var exec = supplier.get();
        return HandleFromResult(exec, statusCode);
    }

    public static <T> ResponseEntity<?> SendResponse(Function<AuthenticatedUser, Result<T>> function){
        return SendResponse(function, HttpStatus.OK);
    }

    public static <T> ResponseEntity<?> SendResponse(Function<AuthenticatedUser, Result<T>> function, HttpStatusCode statusCode) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }

        var exec = function.apply((AuthenticatedUser) auth.getPrincipal());
        return HandleFromResult(exec, statusCode);
    }

    private static ResponseEntity<?> HandleFromResult(Result<?> result, HttpStatusCode successCode) {
        if(result.isSuccess()){
            return ResponseEntity.status(successCode).body(result);
        }

        var error = result.getError();
        HttpStatusCode statusCode;

        switch (error.getErrorType()){
            case NOT_FOUND_ERROR -> statusCode = HttpStatus.NOT_FOUND;
            case OPERATION_ERROR, PROCESSING_ERROR -> statusCode = HttpStatus.UNPROCESSABLE_ENTITY;
            case FAILURE_ERROR, VALIDATION_ERROR -> statusCode = HttpStatus.BAD_REQUEST;
            default -> statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        log.error(error.getDescription().toString());
        return ResponseEntity.status(statusCode).body(result);
    }

}
