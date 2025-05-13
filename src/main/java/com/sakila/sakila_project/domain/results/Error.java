package com.sakila.sakila_project.domain.results;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Error {
    private final List<String> Description;
    private final ErrorType ErrorType;

    public Error(List<String> message, ErrorType errorCode) {
        this.Description = message;
        this.ErrorType = errorCode;
    }

    public Error(String message, ErrorType errorCode) {
        this.Description = new ArrayList<>();
        this.Description.add(message);
        this.ErrorType = errorCode;
    }
}
