package com.sakila.sakila_project.application.usecases.ports.film_operations;

import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;
import com.sakila.sakila_project.application.dto.Film.ExtendedFilmDto;
import com.sakila.sakila_project.application.dto.category.BaseCategoryDto;
import com.sakila.sakila_project.application.dto.category.ExtendedCategoryDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IGetFilmsUseCase {
    public Result<List<BaseCategoryDto>> getFilmsInfo();
    public Result<ExtendedFilmDto> getFilm(String referenceId);
}
