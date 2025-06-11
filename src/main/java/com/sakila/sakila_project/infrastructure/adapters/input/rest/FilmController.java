package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.usecases.ports.film_operations.IGetFilmsUseCase;
import com.sakila.sakila_project.infrastructure.utils.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final IGetFilmsUseCase getFilmsUseCase;

    public FilmController(IGetFilmsUseCase getFilmsUseCase) {
        this.getFilmsUseCase = getFilmsUseCase;
    }

    @GetMapping("/getFilmsWithCategories")
    public ResponseEntity<?> getFilmsWithCategories() {
        return ResponseHandler.SendResponse(this.getFilmsUseCase::getFilms);
    }

}
