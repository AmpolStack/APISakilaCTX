package com.sakila.sakila_project.application.dto.staff;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseStaffDto {
    private String first_name;
    private String last_name;
    private byte[] picture;
    private String email;
    private int active;
    private String username;
    private Date last_update;
}
