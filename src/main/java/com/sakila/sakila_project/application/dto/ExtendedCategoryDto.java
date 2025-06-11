package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.modeler.BaseAttributeFilter;

import java.util.List;

@Getter
@Setter
public class ExtendedCategoryDto {
    private String name;
    private List<BaseFilmDto> films;
}
