package com.sakila.sakila_project.application.usecases.adapters.filmUseCases;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakila.sakila_project.application.dto.film.FilmWithActorsAndCategoriesDto;
import com.sakila.sakila_project.application.dto.category.CategoryWithFilmBasePropertiesDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.CategoryDtoMapper;
import com.sakila.sakila_project.application.maps.FilmDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.film_operations.IGetFilmsUseCase;
import com.sakila.sakila_project.application.utils.EntityLoaderBuilder;
import com.sakila.sakila_project.application.utils.IdTransformer;
import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.CategoryRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.FilmRepository;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import com.sakila.sakila_project.infrastructure.adapters.output.services.CacheService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

@Service
public class GetFilmsUseCase implements IGetFilmsUseCase {

    private final CategoryRepository categoryRepository;
    private final FilmRepository filmRepository;
    private final CategoryDtoMapper categoryMapper;
    private final FilmDtoMapper filmDtoMapper;
    private final CacheService cacheService;

    public GetFilmsUseCase(CategoryRepository categoryRepository,
                           CategoryDtoMapper categoryMapper,
                           FilmRepository filmRepository,
                           FilmDtoMapper filmDtoMapper,
                           CacheService cacheService) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.filmRepository = filmRepository;
        this.filmDtoMapper = filmDtoMapper;
        this.cacheService = cacheService;
    }

    @Override
    public Result<List<CategoryWithFilmBasePropertiesDto>> getFilmsInfo() {
        var categories = categoryRepository.findAllCategoriesWithFilms();
        var mapping = this.categoryMapper.toBaseCategoryDtoList(categories);
        return Result.Success(mapping);
    }

    @Override
    public Result<FilmWithActorsAndCategoriesDto> getFilm(String referenceId) {
        return new EntityLoaderBuilder<Film, FilmWithActorsAndCategoriesDto>()
                .setCacheService(cacheService)
                .setEntity(this.filmRepository::findFilmById)
                .setMappingMethod(this.filmDtoMapper::toFilmWithActorsAndCategoriesDto)
                .setCacheIdentifierWithDto((model) -> Integer.toString(model.getId()))
                .buildWithReferenceId(referenceId, Film.class, FilmWithActorsAndCategoriesDto.class);
    }
}
