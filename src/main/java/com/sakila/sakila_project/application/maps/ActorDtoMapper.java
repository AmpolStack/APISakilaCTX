package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ExtendedActorDto;
import com.sakila.sakila_project.application.dto.ExtendedFilmDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.model.sakila.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ActorDtoMapper {

    @Mapping(target = "language", qualifiedByName = "LanguageObjectToString")
    ExtendedFilmDto toFilmWithLanguageDto(Film film);

    ExtendedActorDto toActorWithFilmDto(Actor dto);

    @Named("LanguageObjectToString")
    default String toLanguageObjectToString(Language language) {
        return language.getName();
    }

}
