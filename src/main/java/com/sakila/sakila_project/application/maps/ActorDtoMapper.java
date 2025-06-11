package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ExtendedActorDto;
import com.sakila.sakila_project.application.dto.ExtendedFilmDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.model.sakila.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring", uses = FilmDtoMapper.class)
public interface ActorDtoMapper {
    ExtendedActorDto toActorWithFilmDto(Actor dto);
}
