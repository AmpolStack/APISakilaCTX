package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.maps.ActorDtoMapper;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.ActorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/actors")
public class ActorController {
    private ActorRepository actorRepository;
    private MinimalDtoMapper minimalDtoMapper;
    private ActorDtoMapper actorDtoMapper;

    @Autowired
    public ActorController(ActorRepository actorRepository, MinimalDtoMapper minimalDtoMapper, ActorDtoMapper actorDtoMapper) {
        this.actorRepository = actorRepository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.actorDtoMapper = actorDtoMapper;
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

}
