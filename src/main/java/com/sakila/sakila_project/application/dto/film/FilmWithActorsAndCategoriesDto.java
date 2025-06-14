package com.sakila.sakila_project.application.dto.film;

import com.sakila.sakila_project.application.dto.actor.ActorDto;
import com.sakila.sakila_project.application.dto.category.CategoryDto;
import com.sakila.sakila_project.domain.model.sakila.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class FilmWithActorsAndCategoriesDto extends FilmDto {
    private Set<ActorDto> actors;
    private Set<CategoryDto> categories;
}
