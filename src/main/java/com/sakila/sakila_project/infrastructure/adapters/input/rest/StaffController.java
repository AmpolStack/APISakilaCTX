package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.Credentials;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.application.maps.MinimalDtoMapperImpl;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private StaffRepository repository;
    private MinimalDtoMapper minimalDtoMapper;
    private StaffDtoMapper staffDtoMapper;


    @Autowired
    public StaffController(StaffRepository repository, MinimalDtoMapper minimalDtoMapper,
                           StaffDtoMapper staffDtoMapper) {
        this.repository = repository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.staffDtoMapper = staffDtoMapper;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity getAllStaffs(){
        try{
            List<Staff> staffs = repository.findAll();
            var maps = this.minimalDtoMapper.toMinStaffDtoList(staffs);
            return ResponseEntity.status(HttpStatus.OK).body(maps);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getByCredentials")
    public ResponseEntity getByCredentials(@RequestBody Credentials credentials){
        try{

            var username = credentials.getUsername();
            var password = credentials.getPassword();

            if(username.isEmpty() || password.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is empty");
            }

            var staffOp = this.repository.findStaffByUsernameAndPassword(username, password);

            if(staffOp.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            var staff = staffOp.get();
            var map = this.minimalDtoMapper.toMinStaffDto(staff);
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getAllInfoByCredentials")
    public ResponseEntity getAllInfoByCredentials(@RequestParam String username, @RequestParam String password){
        try{
            var staffOp = this.repository.findStaffByUsernameAndPassword(username, password);
            if(staffOp.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            var staff = staffOp.get();
            var mapped = this.staffDtoMapper.toDto(staff);
            return ResponseEntity.status(HttpStatus.OK).body(mapped);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
