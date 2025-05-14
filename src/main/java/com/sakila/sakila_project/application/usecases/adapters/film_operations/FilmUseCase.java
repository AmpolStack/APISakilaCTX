package com.sakila.sakila_project.application.usecases.adapters.film_operations;

import com.sakila.sakila_project.application.dto.BaseFilmDto;
import com.sakila.sakila_project.application.dto.ExtendedFilmDto;
import com.sakila.sakila_project.application.usecases.ports.film_operations.IFilmUseCase;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public class FilmUseCase implements IFilmUseCase {
    //TODO: Implements
    @Override
    public Result<ExtendedFilmDto> getFilmById(int id) {
        return null;
    }

    @Override
    public Result<ExtendedFilmDto> CreateFilm(ExtendedFilmDto extendedFilmDto) {
        return null;
    }

    @Override
    public Result<List<BaseFilmDto>> getAllFilmsWithBasicInfo() {
        return null;
    }

    @Override
    public Result<List<ExtendedFilmDto>> getAllFilmsWithExtendedInfo() {
        return null;
    }

    @Override
    public Result<ExtendedFilmDto> UpdateFilm(ExtendedFilmDto extendedFilmDto) {
        return null;
    }

    @Override
    public Result<ExtendedFilmDto> DeleteFilm(int filmId) {
        return null;
    }

    @Override
    public Result<Void> AddActorInFilm(int filmId, String actorId) {
        return null;
    }
}
