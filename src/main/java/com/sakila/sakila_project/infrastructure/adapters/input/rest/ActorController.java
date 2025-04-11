package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.dto.ExtFilmDto;
import com.sakila.sakila_project.application.dto.MinActorDto;
import com.sakila.sakila_project.application.maps.ActorDtoMapper;
import com.sakila.sakila_project.application.maps.FilmDtoMapper;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.ActorRepository;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private ActorRepository actorRepository;
    private FilmRepository filmRepository;
    private MinimalDtoMapper minimalDtoMapper;
    private ActorDtoMapper actorDtoMapper;

    @Autowired
    public ActorController(ActorRepository actorRepository, MinimalDtoMapper minimalDtoMapper, ActorDtoMapper actorDtoMapper,
                           FilmRepository filmRepository) {
        this.actorRepository = actorRepository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.actorDtoMapper = actorDtoMapper;
        this.filmRepository = filmRepository;
    }

    @GetMapping("/getAllActorsInfo")
    public ResponseEntity getAllActors() {
        try{
            var actors = this.actorRepository.findActors();
            var maps = this.minimalDtoMapper.toMinimalActorDtoList(actors);
            return ResponseEntity.ok(maps);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getActorById")
    public ResponseEntity getActorAllInfo(@RequestParam int id) {
        try{
            var actor = this.actorRepository.findById(id);

            if(actor == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
            }

            var map = this.actorDtoMapper.toActorWithFilmDto(actor.get());
            return ResponseEntity.ok(map);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/auth/deleteActorById")
    public ResponseEntity deleteActorById(@RequestParam int id) {
        try{
            if(!this.actorRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
            }

            this.actorRepository.deleteById(id);
            this.actorRepository.flush();

            return ResponseEntity.ok("Actor " + id + " deleted successfully");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PostMapping("/createActor")
    public ResponseEntity createActor(@RequestBody MinActorDto actor,
                                      @RequestParam int id) {
        try{

            if(this.actorRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Actor already exists");
            }
            
            var map = this.minimalDtoMapper.toActor(actor);
            var resp = this.actorRepository.saveAndFlush(map);

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/auth/updateActor")
    public ResponseEntity updateActor(@RequestBody MinActorDto actor,
                                      @RequestParam int id) {
        try{

            if(!this.actorRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
            }
            var map = this.minimalDtoMapper.toActor(actor);
            var resp = this.actorRepository.saveAndFlush(map);

            return ResponseEntity.ok(resp);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/addFilmWithActorById")
    public ResponseEntity addFilm(@RequestParam int actorId, @RequestParam int filmId){
        try{
           var actorOpt = this.actorRepository.findById(actorId);
           if(actorOpt == null){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
           }
           var filmOpt = this.filmRepository.findById(filmId);
           if(filmOpt == null){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Film not found");
           }
           var actor = actorOpt.get();
           var film = filmOpt.get();

           actor.getFilms().add(film);

           var resp = this.actorRepository.save(actor);
           var mapping = this.actorDtoMapper.toActorWithFilmDto(resp);
           return ResponseEntity.ok(mapping);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}