package com.sakila.sakila_project.application.usecases.ports.film_operations;

import com.sakila.sakila_project.application.dto.BaseFilmDto;
import com.sakila.sakila_project.application.dto.ExtendedFilmDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IFilmUseCase {
    Result<ExtendedFilmDto> getFilmById(int id);
    Result<ExtendedFilmDto> CreateFilm(ExtendedFilmDto extendedFilmDto);
    Result<List<BaseFilmDto>> getAllFilmsWithBasicInfo();
    Result<List<ExtendedFilmDto>> getAllFilmsWithExtendedInfo();
    Result<ExtendedFilmDto> UpdateFilm(ExtendedFilmDto extendedFilmDto);
    Result<ExtendedFilmDto> DeleteFilm(int filmId);
    Result<Void> AddActorInFilm(int filmId, String actorId);
}
