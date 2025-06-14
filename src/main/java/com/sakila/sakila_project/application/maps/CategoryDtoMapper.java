package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.category.CategoryDto;
import com.sakila.sakila_project.application.dto.category.CategoryWithFilmBasePropertiesDto;
import com.sakila.sakila_project.application.dto.category.CategoryWithFilmsDto;
import com.sakila.sakila_project.application.utils.IdTransformer;
import com.sakila.sakila_project.domain.model.sakila.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "spring", uses = FilmDtoMapper.class)
public interface CategoryDtoMapper {

    CategoryWithFilmsDto toExtendedCategoryDto(Category category);
    CategoryWithFilmBasePropertiesDto toBaseCategoryDto(Category category);
    List<CategoryWithFilmBasePropertiesDto> toBaseCategoryDtoList(List<Category> categories);

    @Mapping(source = ".", target = "referenceId", qualifiedByName = "ObtainCategoryReferenceId")
    CategoryDto toCategoryDto(Category category);

    @Named("ObtainCategoryReferenceId")
    default String obtainCategoryReferenceId(Category category) {
        return IdTransformer.transform(category.getName(), category.getId());
    }
}
