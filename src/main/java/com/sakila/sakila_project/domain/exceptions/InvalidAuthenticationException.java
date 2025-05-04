package com.sakila.sakila_project.domain.exceptions;

public class InvalidAuthenticationException extends RuntimeException{
    public InvalidAuthenticationException(String message) {
        super(message);
    }
}
