package com.sakila.sakila_project.application.custom.authentication;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthenticationBridge {
    private String token;
    private String refreshToken;
}
