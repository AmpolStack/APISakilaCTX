package com.sakila.sakila_project.domain.results;

import lombok.Getter;

@Getter
public class Error {
    private final String Description;
    private final ErrorCode ErrorCode;

    public Error(String message, ErrorCode errorCode) {
        this.Description = message;
        this.ErrorCode = errorCode;
    }
}
