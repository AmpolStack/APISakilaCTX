package com.sakila.sakila_project.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MinFilmDto {
    private String title;
    private String description;
    private int release_year;
    private int rental_duration;
    private float rental_rate;
    private int length;
    private double replacement_cost;
    private String rating;
    private String special_features;
    private Date last_update;
}
