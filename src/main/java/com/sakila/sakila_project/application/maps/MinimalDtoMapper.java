package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.MinimalActorDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MinimalDtoMapper {

    public MinimalActorDto toMinimalActorDto(Actor dto);

    public List<MinimalActorDto> toMinimalActorDtoList(List<Actor> dtoList);
}
