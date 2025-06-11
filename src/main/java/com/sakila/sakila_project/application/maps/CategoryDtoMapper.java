package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ExtendedCategoryDto;
import com.sakila.sakila_project.domain.model.sakila.Category;
import com.sakila.sakila_project.domain.model.sakila.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = FilmDtoMapper.class)
public interface CategoryDtoMapper {
    ExtendedCategoryDto toDto(Category category);
    List<ExtendedCategoryDto> toDtoList(List<Category> categories);
}
