package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.actor.RequestActorDto;
import com.sakila.sakila_project.application.dto.others.AddressDto;
import com.sakila.sakila_project.application.dto.film.FilmDto;
import com.sakila.sakila_project.application.dto.staff.StaffDto;
import com.sakila.sakila_project.domain.model.sakila.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseDtoMapper {

    RequestActorDto toMinimalActorDto(Actor dto);

    Staff toStaff(StaffDto dto);

    Address toAddress(AddressDto dto);

    List<RequestActorDto> toMinimalActorDtoList(List<Actor> dtoList);

    Actor toActor(RequestActorDto actor);

    StaffDto toMinStaffDto(Staff staff);

    List<StaffDto> toMinStaffDtoList(List<Staff> dtoList);

    @Mapping(source = "language", target = "language", qualifiedByName = "languageToStringLanguage")
    FilmDto toFilmDto(Film film);

    List<FilmDto> toFilmDtoList(List<Film> films);

    @Named("languageToStringLanguage")
    default String languageToStringLanguage(Language language) {
        return language != null ? language.getName() : "";
    }
}
