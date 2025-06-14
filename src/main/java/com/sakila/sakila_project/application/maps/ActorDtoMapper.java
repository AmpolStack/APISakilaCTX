package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.actor.ActorDto;
import com.sakila.sakila_project.application.dto.actor.RequestActorWithFilmsDto;
import com.sakila.sakila_project.application.utils.IdTransformer;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;
import java.util.Set;


@Mapper(componentModel = "spring", uses = {FilmDtoMapper.class, BaseDtoMapper.class})
public interface ActorDtoMapper {


    RequestActorWithFilmsDto toActorWithFilmDto(Actor actor);

    List<RequestActorWithFilmsDto> toExtendedDtoList(List<Actor> dto);

    @Mapping(source = ".", target = "referenceId", qualifiedByName = "ObtainActorReferenceId")
    @Mapping(source = ".", target = "name", qualifiedByName = "ObtainName")
    ActorDto toActorDto(Actor actor);

    Set<ActorDto> toActorDtoList(Set<Actor> actors);

    @Named("ObtainActorReferenceId")
    default String obtainActorReferenceId(Actor actor){
        var paddingContent = actor.getFirst_name() +  actor.getLast_name();
        return IdTransformer.transform(paddingContent, actor.getId());
    }

    @Named("ObtainName")
    default String obtainName(Actor actor){
        return actor.getFirst_name() + actor.getLast_name();
    }


}
