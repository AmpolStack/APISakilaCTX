package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.Film.ExtendedFilmDto;
import com.sakila.sakila_project.application.dto.Film.MinimalFilmDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Film;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BaseDtoMapper.class, ActorDtoMapper.class})
public interface FilmDtoMapper {
    @Mapping(source = ".", target = "referenceId", qualifiedByName = "idNumberToFormat")
    MinimalFilmDto minimalFilmDto(Film film);

    List<MinimalFilmDto> minimalFilmDtoList(List<Film> films);

    @Named("idNumberToFormat")
    default String idNumberToFormat(Film film) {
        var idString = Integer.toString(film.getId());
        var value = film.getTitle() + ":" + idString;
        var valueBytes = value.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(valueBytes);
    }

    @Mapping(source="actors", target = "actorsNames", qualifiedByName = "actorListToStringList")
    @Mapping(source = "language", target="language", qualifiedByName = "languageToString")
    ExtendedFilmDto toExtendedFilmDto(Film film);

    @Named("actorListToStringList")
    default List<String> actorListToStringList(List<Actor> actors) {
        return actors.stream().map((x)->{
            return (x.getFirst_name() + " " + x.getLast_name());
        }).collect(Collectors.toList());
    }

    List<ExtendedFilmDto> toExtendedFilmDtoList(List<Film> films);
}
