package com.sakila.sakila_project.application.custom;

import lombok.Getter;
import lombok.Setter;

@Setter
public class AuthenticatedUserMetadata {
    @Getter
    private int id;
    protected String username;
    @Getter
    private String phone;
    @Getter
    private String email;
}
