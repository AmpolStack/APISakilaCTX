package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendedFilmDto extends BaseFilmDto {
    private String language;
}
