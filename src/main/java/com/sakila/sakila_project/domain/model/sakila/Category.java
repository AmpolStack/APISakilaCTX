package com.sakila.sakila_project.domain.model.sakila;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private int id;
    @Column(nullable = false, length = 25)
    private String name;
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "categories")
    private Set<Film> films;
    private Date last_update;
}
