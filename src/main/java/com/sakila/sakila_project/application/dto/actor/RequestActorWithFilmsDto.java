package com.sakila.sakila_project.application.dto.actor;

import com.sakila.sakila_project.application.dto.film.FilmDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActorWithFilmsDto extends ActorDto {
    private List<FilmDto> films;
}
