package com.sakila.sakila_project.application.usecases.ports.film_operations;

import com.sakila.sakila_project.application.dto.ExtendedCategoryDto;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IGetFilmsUseCase {
    public Result<List<ExtendedCategoryDto>> getFilms();
}
