package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.Language;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmWithLanguageDto extends MinimalFilmDto{
    private String language;
}
