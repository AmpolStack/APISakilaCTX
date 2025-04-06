package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MinActorDto {
    private String first_name;
    private String last_name;
    private Date last_update;
}
