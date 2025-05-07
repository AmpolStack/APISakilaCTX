package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IGetStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IMutableStaffUseCase;
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
    private final IMutableStaffUseCase _mutableStaffUseCase;

    @Autowired
    public StaffController(IAuthStaffUseCase authStaffUseCase,
                           IGetStaffUseCase getStaffUseCase,
                           IMutableStaffUseCase mutableStaffUseCase) {
        _authStaffUseCase = authStaffUseCase;
        _getStaffUseCase = getStaffUseCase;
        _mutableStaffUseCase = mutableStaffUseCase;
    }


    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
        try{
            var resp = _getStaffUseCase.AllWithBasicInfo();
            return ResponseEntity.ok(resp);
        }
        catch(Exception ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateAllBaseProperties")
    public ResponseEntity<?> updateAllProperties(@RequestBody BaseStaffDto dto){
        try{
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }
            var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

            var resp = _mutableStaffUseCase.updateAllStaffProperties(dto, id);
            return ResponseEntity.ok(resp);
        }
        catch (NoSuchElementException ex){
            return ErrorResponse(ex, HttpStatus.NOT_FOUND);
        }
        catch (IllegalStateException ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            return ErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PatchMapping("/updateAddress")
    public ResponseEntity<?> updateAddress(@RequestBody BaseAddressDto dto){
        try{
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }
            var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

            var resp = _mutableStaffUseCase.updateAddresses(dto, id);
            return ResponseEntity.ok(resp);

        }
        catch (NoSuchElementException ex){
            return ErrorResponse(ex, HttpStatus.NOT_FOUND);
        }
        catch (IllegalStateException ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            return ErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @PatchMapping("/updateAssignedStore")
    public ResponseEntity<?> updateAssignedStore(@RequestParam int storeId){
        try{
            var auth = SecurityContextHolder.getContext().getAuthentication();

            if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }
            var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

            _mutableStaffUseCase.updateAssignedStore(storeId, id);

            return ResponseEntity.ok("Successfully updated");
        }
        catch (NoSuchElementException ex){
            return ErrorResponse(ex, HttpStatus.NOT_FOUND);
        }
        catch (IllegalStateException ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            return ErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @GetMapping("/getByIdWithCompleteInfo")
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
            return ErrorResponse(ex, HttpStatus.NOT_FOUND);
        }
        catch (IllegalStateException ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        catch(Exception e){
            return ErrorResponse(e, HttpStatus.SERVICE_UNAVAILABLE);
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

    @GetMapping("/open/getCsrfToken")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(csrfToken);
        }
        catch(Exception ex){
            return ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private ResponseEntity<?> GeneralizedAuthentication(Function<IAuthStaffUseCase, AuthenticationBridge> function){
        try{
            var action = function.apply(_authStaffUseCase);
            return ResponseEntity.status(HttpStatus.OK).body(action);
        }
        catch (InvalidCredentialsException | JwtException ex){
            return ErrorResponse(ex, HttpStatus.BAD_REQUEST);
        }
        catch (InvalidAuthenticationException | TokenExpiredException ex){
            return ErrorResponse(ex, HttpStatus.UNAUTHORIZED);
        }
        catch (Exception ex){
            return ErrorResponse(ex, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }


    private static ResponseEntity<?> ErrorResponse(Exception exception, HttpStatus status){
        log.error(exception.getMessage());
        return ResponseEntity.status(status).body(exception.getMessage());
    }
}
