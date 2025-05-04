package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.maps.MinimalDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.domain.exceptions.InvalidAuthenticationException;
import com.sakila.sakila_project.domain.exceptions.InvalidCredentialsException;
import com.sakila.sakila_project.domain.exceptions.TokenExpiredException;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.Function;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffRepository _repository;
    private final MinimalDtoMapper _minimalDtoMapper;
    private final StaffDtoMapper _staffDtoMapper;
    private final IAuthStaffUseCase _authStaffUseCase;


    @Autowired
    public StaffController(StaffRepository repository,
                           MinimalDtoMapper minimalDtoMapper,
                           StaffDtoMapper staffDtoMapper,
                           IAuthStaffUseCase authStaffUseCase) {
        _repository = repository;
        _minimalDtoMapper = minimalDtoMapper;
        _staffDtoMapper = staffDtoMapper;
        _authStaffUseCase = authStaffUseCase;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
        try{
            List<Staff> staffs = _repository.findAll();
            var maps = _minimalDtoMapper.toMinStaffDtoList(staffs);
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
            var staffOp = _repository.findById(id);

            if(staffOp.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Staff not found");
            }

            var staff = staffOp.get();
            var mapped = _staffDtoMapper.toDto(staff);
            return ResponseEntity.status(HttpStatus.OK).body(mapped);

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/open/obtainAuthentication")
    public ResponseEntity<?> obtainAuthentication(@RequestBody AuthenticationCredentials credentials){
        return GeneralizedAuthentication(x -> x.Authenticate(credentials));
    }

    @PostMapping("/open/refreshAuthentication")
    public ResponseEntity<?> refreshAuthentication(@RequestBody AuthenticationBridge authenticationRequest){
        return GeneralizedAuthentication(x -> x.Authenticate(authenticationRequest));
    }

    private ResponseEntity<?> GeneralizedAuthentication(Function<IAuthStaffUseCase, AuthenticationBridge> function){
        try{
            var action = function.apply(_authStaffUseCase);
            return ResponseEntity.status(HttpStatus.OK).body(action);
        }
        catch (InvalidCredentialsException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (InvalidAuthenticationException | TokenExpiredException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        catch (JwtException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
        }
    }

    @GetMapping("/open/getCsrfToken")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken){
        return ResponseEntity.status(HttpStatus.OK).body(csrfToken);
    }
}
