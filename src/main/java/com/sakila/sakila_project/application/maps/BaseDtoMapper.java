package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.BaseActorDto;
import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.domain.model.sakila.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseDtoMapper {

    BaseActorDto toMinimalActorDto(Actor dto);

    Staff toStaff(BaseStaffDto dto);

    Address toAddress(BaseAddressDto dto);

    List<BaseActorDto> toMinimalActorDtoList(List<Actor> dtoList);

    Actor toActor(BaseActorDto actor);

    BaseStaffDto toMinStaffDto(Staff staff);

    List<BaseStaffDto> toMinStaffDtoList(List<Staff> dtoList);

    @Mapping(source = "language", target = "language", qualifiedByName = "languageToString")
    BaseFilmDto toBaseFilmDto(Film film);

    List<BaseFilmDto> toBaseFilmDtoList(List<Film> films);

    @Named("languageToString")
    default String languageToString(Language language) {
        return language != null ? language.getName() : null;
    }
}
