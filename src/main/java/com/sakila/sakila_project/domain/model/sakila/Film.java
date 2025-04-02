package com.sakila.sakila_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity(name = "film")
public class Film {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "film_id")
    private int id;
    private String title;
    private String description;
    private int release_year;
    @ManyToOne(fetch = FetchType.LAZY)
    private Language language;
    private int rental_duration;
    private double rental_price;
    private int length;
    private double replacement_cost;
    private String rating;
    private String special_features;
    private Date last_update;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Actor> actors;
}
