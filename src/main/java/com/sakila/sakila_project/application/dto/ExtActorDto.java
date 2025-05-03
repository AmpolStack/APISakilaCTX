package com.sakila.sakila_project.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtActorDto extends MinActorDto {
    private List<ExtFilmDto> films;
}
