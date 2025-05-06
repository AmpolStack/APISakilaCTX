package com.sakila.sakila_project.domain.ports.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
    @Query("Select a FROM actor a JOIN fetch a.films f JOIN FETCH f.language")
    List<Actor> findAllWithFilmsAndLanguage();

    @Query("Select a FROM actor a JOIN fetch a.films f JOIN FETCH f.language WHERE a.id = :id")
    Optional<Actor> findByIdWithFilmsAndLanguage(int id);

}
