package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ExtendedActorDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring", uses = BaseDtoMapper.class)
public interface ActorDtoMapper {
    ExtendedActorDto toActorWithFilmDto(Actor dto);

    List<ExtendedActorDto> toExtendedDtoList(List<Actor> dto);
}
