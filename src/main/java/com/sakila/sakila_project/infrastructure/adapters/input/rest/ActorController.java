package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.dto.MinActorDto;
import com.sakila.sakila_project.application.maps.ActorDtoMapper;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.domain.ports.output.IEmailService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.ActorRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.FilmRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Value("${spring.email.username}")
    private String emailSenderAddress;

    @Value("${spring.application.request.expirationTime}")
    private int expirationRequestTime;

    @Value("${spring.application.request.codeLength}")
    private int codeLength;

    private final ActorRepository actorRepository;
    private final FilmRepository filmRepository;
    private final MinimalDtoMapper minimalDtoMapper;
    private final ActorDtoMapper actorDtoMapper;
    private final IEmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ActorController(ActorRepository actorRepository, MinimalDtoMapper minimalDtoMapper, ActorDtoMapper actorDtoMapper,
                           FilmRepository filmRepository, IEmailService emailService,
                           @Qualifier("primaryRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.actorRepository = actorRepository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.actorDtoMapper = actorDtoMapper;
        this.filmRepository = filmRepository;
        this.emailService = emailService;
        this.redisTemplate = redisTemplate;
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
        var randomNumber = generateRandomNumber();
        try{
            this.emailService.SendEmail(
                    "Security code in your created account",
                    "Your security code is: " + randomNumber,
                    emailSenderAddress,
                    email
                    );

            this.redisTemplate.opsForValue().set(email, randomNumber, expirationRequestTime, TimeUnit.MINUTES);
            return ResponseEntity.ok(randomNumber);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/ProveCode")
    public ResponseEntity<?> checkProveCode(@RequestParam int proveCode, @RequestParam String email) {
        try{
            var resp = this.redisTemplate.opsForValue().get(email);

            if(resp == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The code no exist");
            }

            var cast = (String)resp;
            if(!cast.equals(Integer.toString(proveCode))){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The prove code is not equals");
            }

            var x = this.redisTemplate.opsForValue().getAndDelete(email);
            return ResponseEntity.ok("Successfully prove code " + x);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private String generateRandomNumber(){
        var rnd = new Random();
        var max = (int)Math.pow(10, codeLength);
        var number = rnd.nextInt(max);
        return String.format("%0" + codeLength + "d", number);
    }

}