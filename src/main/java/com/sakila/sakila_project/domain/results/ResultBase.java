package com.sakila.sakila_project.domain.results;

import lombok.Getter;

@Getter
public class ResultBase {
    private final boolean Success;
    private final Error Error;

    protected ResultBase(Error error) {
        this.Success = false;
        this.Error = error;
    }

    protected ResultBase() {
        this.Success = true;
        this.Error = null;
    }

}
