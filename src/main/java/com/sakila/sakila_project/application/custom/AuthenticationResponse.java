package com.sakila.sakila_project.application.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResponse extends AuthenticationRequest {
    private String Message;
    private boolean Success;
}
