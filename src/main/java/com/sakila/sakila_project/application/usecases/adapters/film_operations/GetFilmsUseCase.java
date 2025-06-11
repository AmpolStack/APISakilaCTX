package com.sakila.sakila_project.application.usecases.adapters.film_operations;

import com.sakila.sakila_project.application.dto.ExtendedCategoryDto;
import com.sakila.sakila_project.application.maps.CategoryDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.film_operations.IGetFilmsUseCase;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.CategoryRepository;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFilmsUseCase implements IGetFilmsUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper categoryMapper;

    public GetFilmsUseCase(CategoryRepository categoryRepository, CategoryDtoMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Result<List<ExtendedCategoryDto>> getFilms() {
        var categories = categoryRepository.findAllCategoriesWithFilms();
        var mapping = this.categoryMapper.toDtoList(categories);
        return Result.Success(mapping);
    }
}
