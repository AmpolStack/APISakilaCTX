package com.sakila.sakila_project.application.dto.category;

import com.sakila.sakila_project.application.dto.film.FilmBasePropertiesDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryWithFilmBasePropertiesDto {
    private String name;
    private List<FilmBasePropertiesDto> films;
}
