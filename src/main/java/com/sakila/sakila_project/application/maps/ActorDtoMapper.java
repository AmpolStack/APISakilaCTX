package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ActorWithFilmDto;
import com.sakila.sakila_project.application.dto.FilmWithLanguageDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.model.sakila.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ActorDtoMapper {

    @Mapping(target = "language", qualifiedByName = "LanguageObjectToString")
    public FilmWithLanguageDto toFilmWithLanguageDto(Film film);

    public ActorWithFilmDto toActorWithFilmDto(Actor dto);

    @Named("LanguageObjectToString")
    default String toLanguageObjectToString(Language language) {
        return language.getName();
    }

}
