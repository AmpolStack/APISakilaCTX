package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtendedActorDto extends BaseActorDto {
    private List<ExtendedFilmDto> films;
}
