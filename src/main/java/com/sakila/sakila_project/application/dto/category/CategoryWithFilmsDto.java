package com.sakila.sakila_project.application.dto.category;

import com.sakila.sakila_project.application.dto.film.FilmDto;

import java.util.List;
import java.util.Set;

public class CategoryWithFilmsDto {
    private String name;
    private Set<FilmDto> films;
}
