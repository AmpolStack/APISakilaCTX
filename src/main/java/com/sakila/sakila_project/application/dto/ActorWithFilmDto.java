package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;
import org.mapstruct.Mapping;

import java.util.List;

@Getter
@Setter
public class ActorWithFilmDto extends MinimalActorDto{
    private List<FilmWithLanguageDto> films;
}
