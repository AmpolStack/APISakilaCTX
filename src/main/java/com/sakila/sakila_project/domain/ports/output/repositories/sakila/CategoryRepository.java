package com.sakila.sakila_project.domain.ports.output.repositories.sakila;

import com.sakila.sakila_project.domain.model.sakila.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    @Query("SELECT c FROM Category c JOIN FETCH c.films")
    List<Category> findAllCategoriesWithFilms();

    @Query("SELECT c FROM Category c JOIN FETCH c.films f JOIN FETCH f.language")
    List<Category> findAllCategoriesWithFilmsAndLanguage();

}
