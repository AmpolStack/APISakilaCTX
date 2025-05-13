package com.sakila.sakila_project.domain.results;

import lombok.Getter;

public class Result<T> extends ResultBase {
    @Getter
    private final T Data;

    public Result(){
        super();
        this.Data = null;
    }

    public Result(T data) {
        super();
        this.Data = data;
    }

    public Result(Error error) {
        super(error);
        this.Data = null;
    }

    public static <T> Result<T> Success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> Failed(Error error) {
        return new Result<>(error);
    }

    public static <T> Result<T> Success() {
        return new Result<>();
    }

}


