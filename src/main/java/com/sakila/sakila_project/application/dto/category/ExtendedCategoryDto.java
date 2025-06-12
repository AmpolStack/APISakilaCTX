package com.sakila.sakila_project.application.dto.category;

import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;

import java.util.List;

public class ExtendedCategoryDto {
    private String name;
    private List<BaseFilmDto> films;
}
