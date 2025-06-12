package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.category.BaseCategoryDto;
import com.sakila.sakila_project.application.dto.category.ExtendedCategoryDto;
import com.sakila.sakila_project.domain.model.sakila.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = FilmDtoMapper.class)
public interface CategoryDtoMapper {

    ExtendedCategoryDto toExtendedCategoryDto(Category category);
    BaseCategoryDto toBaseCategoryDto(Category category);
    List<BaseCategoryDto> toBaseCategoryDtoList(List<Category> categories);
}
