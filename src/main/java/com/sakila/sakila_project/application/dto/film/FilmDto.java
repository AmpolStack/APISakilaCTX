package com.sakila.sakila_project.application.dto.film;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilmDto {
    private int id;
    private String title;
    private String description;
    private int release_year;
    private int rental_duration;
    private float rental_rate;
    private int length;
    private double replacement_cost;
    private String rating;
    private String special_features;
    private String language;
}
