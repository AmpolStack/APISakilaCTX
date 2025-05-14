package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.Actor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class ExtendedFilmDto extends BaseFilmDto {
    private Set<Actor> actors;
}
