package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;
import com.sakila.sakila_project.application.dto.Film.MinimalFilmDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtendedCategoryDto {
    private String name;
    private List<MinimalFilmDto> films;
}
