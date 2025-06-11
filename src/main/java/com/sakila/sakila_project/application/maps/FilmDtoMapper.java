package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.BaseFilmDto;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.model.sakila.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FilmDtoMapper {
    @Mapping(source = "language", target = "language", qualifiedByName = "languageToString")
    BaseFilmDto toDto(Film film);

    List<BaseFilmDto> toDtoList(List<Film> films);

    @Named("languageToString")
    default String languageToString(Language language) {
        return language != null ? language.getName() : null;
    }
}
