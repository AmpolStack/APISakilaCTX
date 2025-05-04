package com.sakila.sakila_project.application.custom.authentication;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {
    private String token;
    private String refreshToken;
}
