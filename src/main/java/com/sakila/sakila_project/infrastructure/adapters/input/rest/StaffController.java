package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticatedUser;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IGetStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IMutableStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.IPasswordUseCase;
import com.sakila.sakila_project.domain.results.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.function.Function;

@Slf4j
@RestController
@RequestMapping("/staff")
public class StaffController {

    private final IGetStaffUseCase getStaffUseCase;
    private final IAuthStaffUseCase authStaffUseCase;
    private final IMutableStaffUseCase mutableStaffUseCase;
    private final IPasswordUseCase passwordUseCase;

    @Value("${spring.email.username}")
    private String emailFrom;

    @Autowired
    public StaffController(IAuthStaffUseCase authStaffUseCase,
                           IGetStaffUseCase getStaffUseCase,
                           IMutableStaffUseCase mutableStaffUseCase,
                           IPasswordUseCase passwordUseCase) {
        this.authStaffUseCase = authStaffUseCase;
        this.getStaffUseCase = getStaffUseCase;
        this.mutableStaffUseCase = mutableStaffUseCase;
        this.passwordUseCase = passwordUseCase;
    }

    @GetMapping("/requestPasswordUpdate")
    public ResponseEntity<?> requestPasswordUpdate(@RequestParam String newPassword) {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }
        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.passwordUseCase.SendRequestForUpdatePassword(id, newPassword, this.emailFrom);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }
        return ResponseEntity.ok(resp);
    }

    @PatchMapping("UpdatePasswordByCorrelationId")
    public ResponseEntity<?> updatePasswordByCorrelationId(@RequestParam String correlationId){

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }

        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.passwordUseCase.UpdatePassword(id, correlationId);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
            var resp = this.getStaffUseCase.AllWithBasicInfo();

            if (!resp.isSuccess()) {
                return ErrorResponse(resp);
            }

            return ResponseEntity.ok(resp);
    }

    @PutMapping("/updateAllBaseProperties")
    public ResponseEntity<?> updateAllProperties(@RequestBody BaseStaffDto dto){

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }
        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.mutableStaffUseCase.updateAllStaffProperties(dto, id);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }

        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/updateAddress")
    public ResponseEntity<?> updateAddress(@RequestBody BaseAddressDto dto){
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }
        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.mutableStaffUseCase.updateAddresses(dto, id);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }

        return ResponseEntity.ok(resp);
    }

    @PatchMapping("/updateAssignedStore")
    public ResponseEntity<?> updateAssignedStore(@RequestParam int storeId){

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
        }
        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.mutableStaffUseCase.updateAssignedStore(storeId, id);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/getByIdWithCompleteInfo")
    public ResponseEntity<?> getAllInfo(){
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(!(auth.getPrincipal() instanceof AuthenticatedUser)){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("This user not have permission to access this resource");
            }
        var id = ((AuthenticatedUser) auth.getPrincipal()).getId();

        var resp = this.getStaffUseCase.WithCompleteInfo(id);

        if (!resp.isSuccess()) {
            return ErrorResponse(resp);
        }

        return ResponseEntity.ok(resp);
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
            return ResponseEntity.status(HttpStatus.OK).body(Result.Success(csrfToken));
        }
        catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    private ResponseEntity<?> GeneralizedAuthentication(Function<IAuthStaffUseCase, Result<AuthenticationBridge>> function){
        var actionResult = function.apply(this.authStaffUseCase);
        if (!actionResult.isSuccess()){
            return ErrorResponse(actionResult);
        }
        return ResponseEntity.status(HttpStatus.OK).body(actionResult);
    }

    private static ResponseEntity<?> ErrorResponse(Result<?> result){
        var error = result.getError();
        HttpStatusCode statusCode;

        switch (error.getErrorCode()){
            case NOT_FOUND_ERROR -> statusCode = HttpStatus.NOT_FOUND;
            case OPERATION_ERROR, PROCESSING_ERROR -> statusCode = HttpStatus.UNPROCESSABLE_ENTITY;
            case FAILURE_ERROR, VALIDATION_ERROR -> statusCode = HttpStatus.BAD_REQUEST;
            default -> statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        log.error(error.getDescription().toString());
        return ResponseEntity.status(statusCode).body(result);
    }
}
