package com.sakila.sakila_project.domain.ports.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Integer> {

    @Query("SELECT f FROM film f JOIN FETCH f.language JOIN FETCH f.actors WHERE f.id = :id")
    Optional<Film> findFilmById(int id);
}
