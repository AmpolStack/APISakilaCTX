package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.dto.MinActorDto;
import com.sakila.sakila_project.application.maps.ActorDtoMapper;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.domain.adapters.input.IEmailService;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.ActorRepository;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Value("${email.Username}")
    private String emailSenderAddress;

    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final MinimalDtoMapper minimalDtoMapper;
    private final ActorDtoMapper actorDtoMapper;
    private final IEmailService emailService;

    @Autowired
    public ActorController(ActorRepository actorRepository, MinimalDtoMapper minimalDtoMapper, ActorDtoMapper actorDtoMapper,
                           FilmRepository filmRepository, IEmailService emailService) {
        this.actorRepository = actorRepository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.actorDtoMapper = actorDtoMapper;
        this.filmRepository = filmRepository;
        this.emailService = emailService;
    }

    @GetMapping("/getAllActorsInfo")
    public ResponseEntity<?> getAllActors() {
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
    public ResponseEntity<?> getActorAllInfo(@RequestParam int id) {
        try{
            var actor = this.actorRepository.findById(id);

            if(actor.isEmpty()){
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
    public ResponseEntity<?> deleteActorById(@RequestParam int id) {
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


    @PostMapping("/auth/createActor")
    public ResponseEntity<?> createActor(@RequestBody MinActorDto actor) {
        try{

            var map = this.minimalDtoMapper.toActor(actor);
            var resp = this.actorRepository.saveAndFlush(map);

            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/auth/updateActor")
    public ResponseEntity<?> updateActor(@RequestBody MinActorDto actor,
                                      @RequestParam int id) {
        try{

            if(!this.actorRepository.existsById(id)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
            }
            var map = this.minimalDtoMapper.toActor(actor);
            map.setId(id);
            var resp = this.actorRepository.saveAndFlush(map);

            return ResponseEntity.ok(resp);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/auth/addFilmWithActorById")
    public ResponseEntity<?> addFilm(@RequestParam int actorId, @RequestParam int filmId){
        try{
           var actorOpt = this.actorRepository.findById(actorId);
           if(actorOpt.isEmpty()){
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Actor not found");
           }
           var filmOpt = this.filmRepository.findById(filmId);
           if(filmOpt.isEmpty()){
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

    @GetMapping("/CheckEmail")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        var randomNumber = generateRandomNumber(8);
        try{
            this.emailService.SendEmail(
                    "Security code in your created account",
                    "Your security code is: " + randomNumber,
                    emailSenderAddress,
                    email
                    );
            return ResponseEntity.ok(randomNumber);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private static String generateRandomNumber(int length){
        var rnd = new Random();
        var max = (int)Math.pow(10,length);
        var number = rnd.nextInt(max);
        return String.format("%0" + length + "d", number);
    }

}