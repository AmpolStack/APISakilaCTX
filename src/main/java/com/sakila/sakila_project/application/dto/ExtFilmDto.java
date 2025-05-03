package com.sakila.sakila_project.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtFilmDto extends MinFilmDto {
    private String language;
}
