package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.MinActorDto;
import com.sakila.sakila_project.application.dto.MinStaffDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MinimalDtoMapper {

    MinActorDto toMinimalActorDto(Actor dto);

    List<MinActorDto> toMinimalActorDtoList(List<Actor> dtoList);

    Actor toActor(MinActorDto actor);

    MinStaffDto toMinStaffDto(Staff staff);

    List<MinStaffDto> toMinStaffDtoList(List<Staff> dtoList);
}
