package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IGetStaffUseCase;
import com.sakila.sakila_project.domain.exceptions.InvalidAuthenticationException;
import com.sakila.sakila_project.domain.exceptions.InvalidCredentialsException;
import com.sakila.sakila_project.domain.exceptions.TokenExpiredException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {

    private final IGetStaffUseCase _getStaffUseCase;
    private final IAuthStaffUseCase _authStaffUseCase;


    @Autowired
    public StaffController(IAuthStaffUseCase authStaffUseCase,
                           IGetStaffUseCase getStaffUseCase) {
        _authStaffUseCase = authStaffUseCase;
        _getStaffUseCase = getStaffUseCase;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
        try{
            var resp = _getStaffUseCase.AllWithBasicInfo();
            return ResponseEntity.ok(resp);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error, please try again later");
        }
    }


    @GetMapping("/getAllInfoById")
    public ResponseEntity<?> getAllInfo(){
        try{

            var auth = SecurityContextHolder.getContext().getAuthentication();

            if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }

            var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

            var resp = _getStaffUseCase.WithCompleteInfo(id);
            return ResponseEntity.ok(resp);
        }
        catch (NoSuchElementException ex){
            log.error(ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user are not exist");
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error, please try again later");
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
