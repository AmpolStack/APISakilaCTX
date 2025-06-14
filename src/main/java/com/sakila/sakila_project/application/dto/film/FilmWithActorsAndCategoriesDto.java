package com.sakila.sakila_project.application.dto.film;

import com.sakila.sakila_project.application.dto.actor.ActorDto;
import com.sakila.sakila_project.domain.model.sakila.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FilmWithActorsDto extends FilmDto {
    private List<ActorDto> actors;
    private List<Category> categories;
}
