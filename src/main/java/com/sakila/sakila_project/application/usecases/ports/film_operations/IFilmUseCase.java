package com.sakila.sakila_project.application.usecases.ports.film_operations;

import com.sakila.sakila_project.application.dto.film.FilmDto;
import com.sakila.sakila_project.application.dto.film.FilmWithActorsAndCategoriesDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IFilmUseCase {
    Result<FilmWithActorsAndCategoriesDto> getFilmById(int id);
    Result<FilmWithActorsAndCategoriesDto> CreateFilm(FilmWithActorsAndCategoriesDto extendedFilmDto);
    Result<List<FilmDto>> getAllFilmsWithBasicInfo();
    Result<List<FilmWithActorsAndCategoriesDto>> getAllFilmsWithExtendedInfo();
    Result<FilmWithActorsAndCategoriesDto> UpdateFilm(FilmWithActorsAndCategoriesDto extendedFilmDto);
    Result<FilmWithActorsAndCategoriesDto> DeleteFilm(int filmId);
    Result<Void> AddActorInFilm(int filmId, String actorId);
}
