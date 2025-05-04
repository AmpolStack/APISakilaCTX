package com.sakila.sakila_project.application.custom.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationParams {
    private int TokenExpiration;
    private int RefreshTokenExpiration;
    private String Secret;
}
