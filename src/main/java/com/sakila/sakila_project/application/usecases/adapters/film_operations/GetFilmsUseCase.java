package com.sakila.sakila_project.application.usecases.adapters.film_operations;

import com.sakila.sakila_project.application.dto.Film.BaseFilmDto;
import com.sakila.sakila_project.application.dto.Film.ExtendedFilmDto;
import com.sakila.sakila_project.application.dto.category.BaseCategoryDto;
import com.sakila.sakila_project.application.dto.category.ExtendedCategoryDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.CategoryDtoMapper;
import com.sakila.sakila_project.application.maps.FilmDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.film_operations.IGetFilmsUseCase;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.CategoryRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.FilmRepository;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
public class GetFilmsUseCase implements IGetFilmsUseCase {

    private final CategoryRepository categoryRepository;
    private final FilmRepository filmRepository;
    private final CategoryDtoMapper categoryMapper;
    private final FilmDtoMapper filmDtoMapper;
    private final BaseDtoMapper baseDtoMapper;

    public GetFilmsUseCase(CategoryRepository categoryRepository,
                           CategoryDtoMapper categoryMapper,
                           FilmRepository filmRepository, FilmDtoMapper filmDtoMapper,
                           BaseDtoMapper baseDtoMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.filmRepository = filmRepository;
        this.filmDtoMapper = filmDtoMapper;
        this.baseDtoMapper = baseDtoMapper;
    }

    @Override
    public Result<List<BaseCategoryDto>> getFilmsInfo() {
        var categories = categoryRepository.findAllCategoriesWithFilms();
        var mapping = this.categoryMapper.toBaseCategoryDtoList(categories);
        return Result.Success(mapping);
    }

    @Override
    public Result<ExtendedFilmDto> getFilm(String referenceId) {

        byte[] bytes;

        try{
            bytes = Base64.getDecoder().decode(referenceId);
        }
        catch (IllegalArgumentException e){
            return Result.Failed(new Error("The id format is not valid", ErrorType.OPERATION_ERROR));
        }

        var values = new String(bytes, StandardCharsets.UTF_8).split(":");

        if(values.length < 2 ){
            return Result.Failed(new Error("The id format is not valid", ErrorType.OPERATION_ERROR));
        }

        var idString = values[values.length - 1];
        var id = Integer.parseInt(idString);

        var film = this.filmRepository.findFilmById(id).orElse(null);
        if(film == null) {
            return Result.Failed(new Error("No exist film with this id", ErrorType.NOT_FOUND_ERROR));
        }
        var mapping = this.filmDtoMapper.toExtendedFilmDto(film);
        return Result.Success(mapping);
    }
}
