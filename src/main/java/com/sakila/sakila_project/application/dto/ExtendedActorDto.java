package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtendedActorDto extends BaseActorDto {
    private List<BaseFilmDto> films;
}
