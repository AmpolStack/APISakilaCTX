package com.sakila.sakila_project.Controllers;

import com.sakila.sakila_project.domain.model.sakila.Film;
import com.sakila.sakila_project.domain.model.sakila.Language;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.ActorRepository;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private ActorRepository actorRepository;
    private FilmRepository filmRepository;

    @Autowired
    public ActorController(ActorRepository actorRepository, FilmRepository filmRepository) {
        this.actorRepository = actorRepository;
        this.filmRepository = filmRepository;
    }

    @GetMapping("/getAllActors")
    public ResponseEntity getAllActors() {
        try{
            var resp = this.actorRepository.findAll();
            return ResponseEntity.ok(resp);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllFilmsWithLanguages")
    public ResponseEntity getAllFilmsWithLanguages() {

        try{
            var resp = this.filmRepository.findAll();
            List<subfilm> films = new ArrayList<>();

            resp.forEach(film -> {
                var mapped = map(film);
                films.add(mapped);
            });

            return ResponseEntity.ok(films);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllFilmsWithoutLanguages")
    public ResponseEntity getAllFilmsWithoutLanguages() {
        var resp = this.filmRepository.findAll();
        List<subfilmWithoutLanguage> films = new ArrayList<>();
        resp.forEach(film -> {
           var mapped = map2(film);
           films.add(mapped);
        });
        return ResponseEntity.ok(films);
    }

    private subfilm map(Film film) {
        var temp = new subfilm();
        temp.id = film.getId();
        temp.title = film.getTitle();
        temp.description = film.getDescription();
        temp.release_year = film.getRelease_year();
        temp.rental_duration = film.getRental_duration();
        temp.rental_rate = film.getRental_rate();
        temp.length = film.getLength();
        temp.language = new Language();
        temp.language.setId(film.getLanguage().getId());
        temp.language.setName(film.getLanguage().getName());
        temp.language.setLast_update(film.getLanguage().getLast_update());
        temp.replacement_cost = film.getReplacement_cost();
        temp.rating = film.getRating();
        temp.special_features = film.getSpecial_features();
        temp.last_update = film.getLast_update();
        return temp;
    }

    private subfilmWithoutLanguage map2(Film film) {
        var temp = new subfilmWithoutLanguage();
        temp.id = film.getId();
        temp.title = film.getTitle();
        temp.description = film.getDescription();
        temp.release_year = film.getRelease_year();
        temp.rental_duration = film.getRental_duration();
        temp.rental_rate = film.getRental_rate();
        temp.length = film.getLength();
        temp.replacement_cost = film.getReplacement_cost();
        temp.rating = film.getRating();
        temp.special_features = film.getSpecial_features();
        temp.last_update = film.getLast_update();
        return temp;
    }


    class subfilm extends subfilmWithoutLanguage{
        public Language language;
    }

    class subfilmWithoutLanguage{
        public int id;
        public String title;
        public String description;
        public int release_year;
        public int rental_duration;
        public float rental_rate;
        public int length;
        public double replacement_cost;
        public String rating;
        public String special_features;
        public Date last_update;
    }

}
