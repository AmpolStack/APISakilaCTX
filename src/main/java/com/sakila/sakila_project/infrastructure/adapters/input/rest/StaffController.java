package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.domain.ports.input.IJwtService;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Value("${spring.security.jwt.token.expirationMs}")
    private int tokenExpiration;

    @Value("${spring.security.jwt.refresh.expirationMs}")
    private int refreshTokenExpiration;

    private final StaffRepository repository;
    private final MinimalDtoMapper minimalDtoMapper;
    private final StaffDtoMapper staffDtoMapper;
    private final IJwtService jwtService;


    @Autowired
    public StaffController(StaffRepository repository,
                           MinimalDtoMapper minimalDtoMapper,
                           StaffDtoMapper staffDtoMapper,
                           IJwtService jwtService) {
        this.repository = repository;
        this.minimalDtoMapper = minimalDtoMapper;
        this.staffDtoMapper = staffDtoMapper;
        this.jwtService = jwtService;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
        try{
            List<Staff> staffs = repository.findAll();
            var maps = this.minimalDtoMapper.toMinStaffDtoList(staffs);
            return ResponseEntity.status(HttpStatus.OK).body(maps);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/getAllInfoById")
    public ResponseEntity<?> getAllInfoByCredentials(){
        try{

            var auth = SecurityContextHolder.getContext().getAuthentication();

            if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }

            var id = ((AuthenticatedUser) auth.getPrincipal()).getId();
            var staffOp = this.repository.findById(id);

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

    @PostMapping("/open/obtainAuthentication")
    public ResponseEntity<?> obtainAuthentication(@RequestBody AuthenticationCredentials credentials){
        try{
            var auth = this.jwtService.AuthenticateByCredentials(credentials, refreshTokenExpiration, tokenExpiration);
            if(!auth.isSuccess()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(auth.getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(auth);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/open/refreshAuthentication")
    public ResponseEntity<?> refreshAuthentication(@RequestBody AuthenticationBridge authenticationRequest){
        try{
            var auth = this.jwtService.AuthenticateByRefreshToken(authenticationRequest, tokenExpiration, refreshTokenExpiration);
            if(!auth.isSuccess()){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(auth.getMessage());
            }

            return ResponseEntity.status(HttpStatus.OK).body(auth);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/open/getCsrfToken")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken){
        return ResponseEntity.status(HttpStatus.OK).body(csrfToken);
    }
}
