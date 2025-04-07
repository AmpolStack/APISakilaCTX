package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.infrastructure.adapters.output.repositories.sakila.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private StaffRepository repository;
    private MinimalDtoMapper mapper;

    @Autowired
    public StaffController(StaffRepository repository, MinimalDtoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity getAllStaffs(){
        try{
            List<Staff> staffs = repository.findAll();
            var maps = this.mapper.toMinStaffDtoList(staffs);
            return ResponseEntity.status(HttpStatus.OK).body(maps);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/getByCredentials")
    public ResponseEntity getByCredentials(@RequestParam String username, @RequestParam String password){
        try{

            if(username.isEmpty() || password.isEmpty()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username or password is empty");
            }

            var staffOp = this.repository.findStaffByUsernameAndPassword(username, password);
            if(staffOp.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            var staff = staffOp.get();
            var map = this.mapper.toMinStaffDto(staff);
            return ResponseEntity.status(HttpStatus.OK).body(map);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
