package com.sakila.sakila_project.application.adapters.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {
}
