package com.sakila.sakila_project.application.dto.Film;

import com.sakila.sakila_project.domain.model.sakila.Actor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExtendedFilmDto extends BaseFilmDto {
    private List<String> actorsNames;
}
