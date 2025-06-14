package com.sakila.sakila_project.application.usecases.ports.film_operations;

import com.sakila.sakila_project.application.dto.film.FilmWithActorsAndCategoriesDto;
import com.sakila.sakila_project.application.dto.category.CategoryWithFilmBasePropertiesDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IGetFilmsUseCase {
    public Result<List<CategoryWithFilmBasePropertiesDto>> getFilmsInfo();
    public Result<FilmWithActorsAndCategoriesDto> getFilm(String referenceId);
}
