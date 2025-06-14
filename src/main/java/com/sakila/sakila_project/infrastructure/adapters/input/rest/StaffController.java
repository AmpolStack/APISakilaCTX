package com.sakila.sakila_project.infrastructure.adapters.input.rest;

import com.sakila.sakila_project.application.custom.authentication.AuthenticationBridge;
import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;
import com.sakila.sakila_project.application.dto.others.AddressDto;
import com.sakila.sakila_project.application.dto.staff.StaffDto;
import com.sakila.sakila_project.application.usecases.ports.staff_operations.IAuthStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.staff_operations.IGetStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.staff_operations.IMutableStaffUseCase;
import com.sakila.sakila_project.application.usecases.ports.staff_operations.IPasswordUseCase;
import com.sakila.sakila_project.domain.results.Result;
import com.sakila.sakila_project.infrastructure.utils.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

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
        return ResponseHandler.SendResponse((authCtx) ->{
            var id = authCtx.getId();
            return this.passwordUseCase.SendRequestForUpdatePassword(id, newPassword, this.emailFrom);
        });
    }

    @PatchMapping("UpdatePasswordByCorrelationId")
    public ResponseEntity<?> updatePasswordByCorrelationId(@RequestParam String correlationId){
        return ResponseHandler.SendResponse((authCtx) ->{
            var id = authCtx.getId();
            return this.passwordUseCase.UpdatePassword(id, correlationId);
        });
    }

    @GetMapping("/getAllStaffs")
    public ResponseEntity<?> getAllStaffs(){
        return ResponseHandler.SendResponse(this.getStaffUseCase::AllWithBasicInfo);
    }

    @PutMapping("/updateAllBaseProperties")
    public ResponseEntity<?> updateAllProperties(@RequestBody StaffDto dto){
        return ResponseHandler.SendResponse((authCtx) -> {
           var id = authCtx.getId();
           return this.mutableStaffUseCase.updateAllStaffProperties(dto, id);
        });
    }

    @PatchMapping("/updateAddress")
    public ResponseEntity<?> updateAddress(@RequestBody AddressDto dto){
        return ResponseHandler.SendResponse((authCtx) ->{
           var id = authCtx.getId();
           return this.mutableStaffUseCase.updateAddresses(dto, id);
        });
    }

    @PatchMapping("/updateAssignedStore")
    public ResponseEntity<?> updateAssignedStore(@RequestParam int storeId){
        return ResponseHandler.SendResponse((authCtx) ->{
           var id = authCtx.getId();
           return this.mutableStaffUseCase.updateAssignedStore(storeId, id);
        });
    }

    @GetMapping("/getByIdWithCompleteInfo")
    public ResponseEntity<?> getAllInfo(){
        return ResponseHandler.SendResponse((authCtx) ->{
           var id = authCtx.getId();
           return this.getStaffUseCase.WithCompleteInfo(id);
        });
    }

    @PostMapping("/open/obtainAuthentication")
    public ResponseEntity<?> obtainAuthentication(@RequestBody AuthenticationCredentials credentials){
        return ResponseHandler.SendResponse(
                () -> this.authStaffUseCase.Authenticate(credentials));
    }

    @PostMapping("/open/refreshAuthentication")
    public ResponseEntity<?> refreshAuthentication(@RequestBody AuthenticationBridge authenticationRequest){
        return ResponseHandler.SendResponse(
                () -> this.authStaffUseCase.Authenticate(authenticationRequest));
    }

    @GetMapping("/open/getCsrfToken")
    public ResponseEntity<?> getCsrfToken(CsrfToken csrfToken){
        return ResponseEntity.status(HttpStatus.OK).body(Result.Success(csrfToken));
    }

}
