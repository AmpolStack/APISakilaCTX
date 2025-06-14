package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.film.FilmWithActorsAndCategoriesDto;
import com.sakila.sakila_project.application.dto.film.FilmBasePropertiesDto;
import com.sakila.sakila_project.application.utils.IdTransformer;
import com.sakila.sakila_project.domain.model.sakila.Film;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {BaseDtoMapper.class, ActorDtoMapper.class})
public interface FilmDtoMapper {

    @Mapping(source = ".", target = "referenceId", qualifiedByName = "ObtainFilmReferenceId")
    FilmBasePropertiesDto toFilmBasePropertiesDto(Film film);

    List<FilmBasePropertiesDto> toFilmBasePropertiesDtoList(List<Film> films);

    @Named("ObtainFilmReferenceId")
    default String ObtainReferenceId(Film film) {
        return IdTransformer.transform(film.getTitle(), film.getId());
    }

    @Mapping(source = "language", target="language", qualifiedByName = "languageToStringLanguage")
    FilmWithActorsAndCategoriesDto toFilmWithActorsAndCategoriesDto(Film film);


    Set<FilmWithActorsAndCategoriesDto> toFilmWithActorsAndCategoriesDto(Set<Film> films);

}
