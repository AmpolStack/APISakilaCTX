package com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActorRepository extends JpaRepository<Actor, Integer> {
}
