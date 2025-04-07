package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.MinActorDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MinimalDtoMapper {

    public MinActorDto toMinimalActorDto(Actor dto);

    public List<MinActorDto> toMinimalActorDtoList(List<Actor> dtoList);

    Actor toActor(MinActorDto actor);
}
