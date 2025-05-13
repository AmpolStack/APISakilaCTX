package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.custom.authentication.*;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import com.sakila.sakila_project.domain.ports.input.IJwtService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.tokens.TokenRegistrationRepository;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthStaffUseCase implements IAuthStaffUseCase {

    private final AuthenticationParams authenticationParams;
    private final TokenRegistrationRepository tokenRegistrationRepository;
    private final IJwtService jwtService;
    private final StaffRepository staffRepository;

    @Autowired
    public AuthStaffUseCase(IJwtService jwtService,
                            TokenRegistrationRepository tokenRegistrationRepository,
                            StaffRepository staffRepository,
                            AuthenticationParams authenticationParams) {
        this.jwtService = jwtService;
        this.tokenRegistrationRepository = tokenRegistrationRepository;
        this.staffRepository = staffRepository;
        this.authenticationParams = authenticationParams;
    }

    @Override
    public Result<AuthenticationBridge> Authenticate(AuthenticationCredentials credentials) {

        if(credentials.getUsername().isEmpty()
                || credentials.getPassword().isEmpty()
                || credentials.getUsername().isBlank()
                || credentials.getPassword().isBlank()) {
            return Result.Failed(new Error("username and password are required", ErrorType.VALIDATION_ERROR));
        }

        var staffOp = this.staffRepository.findByUsernameAndPasswordWithAddress(credentials.getUsername(), credentials.getPassword());
        var staff = staffOp.orElse(null);
        if(staff == null) {
            return Result.Failed(new Error("No exist staff with this credentials", ErrorType.NOT_FOUND_ERROR));
        }
        return SaveRegistry(staff);
    }

    @Override
    public Result<AuthenticationBridge> Authenticate(AuthenticationBridge authenticationRequest) {

        Optional<TokenRegistration> registrationOp;
        registrationOp = this.tokenRegistrationRepository.findByTokenAndRefreshToken(
                    authenticationRequest.getToken(),
                    authenticationRequest.getRefreshToken());

        var registration = registrationOp.orElse(null);

        if(registration == null) {
            return Result.Failed(new Error("No exist staff with this credentials", ErrorType.NOT_FOUND_ERROR));
        }

        if(registration.getExpirationDate().before(new Date())){
            return Result.Failed(new Error("Refresh Token has expired, please login again", ErrorType.VALIDATION_ERROR));
        }

        var staffOp = this.staffRepository.findByIdWithAddress(registration.getIdUser());
        var staff = staffOp.orElse(null);

        if(staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        return SaveRegistry(staff);
    }

    @Override
    public Result<AuthenticatedUserMetadata> GetMetadata(String token) {
        var claimsResult = this.jwtService.GetAllClaims(token, this.authenticationParams.getSecret());

        if(!claimsResult.isSuccess()){
            return Result.Failed(claimsResult);
        }

        var claims = claimsResult.getData();
        var authenticatedUserMetadata = new AuthenticatedUserMetadata();
        authenticatedUserMetadata.setUsername(claims.getSubject());
        authenticatedUserMetadata.setId(Integer.parseInt(claims.getId()));
        authenticatedUserMetadata.setEmail(claims.get("email", String.class));
        authenticatedUserMetadata.setPhone(claims.get("phone", String.class));
        return Result.Success(authenticatedUserMetadata);
    }


    private Result<AuthenticationBridge> SaveRegistry(Staff staff) {

        var claims = StaffEntityToClaimsMap(staff);
        var tokenResult = this.jwtService.GenerateToken(claims, this.authenticationParams.getTokenExpiration(), this.authenticationParams.getSecret());

        if(!tokenResult.isSuccess()){
            return Result.Failed(tokenResult);
        }

        var token = tokenResult.getData();
        var refreshToken = this.jwtService.GenerateRefreshToken();

        var tokenRegistration = new TokenRegistration();
        tokenRegistration.setToken(token);
        tokenRegistration.setRefreshToken(refreshToken);
        tokenRegistration.setActive(true);
        tokenRegistration.setIdUser(staff.getId());
        tokenRegistration.setCreationDate(new Date());
        tokenRegistration.setExpirationDate(new Date(new Date().getTime() + this.authenticationParams.getRefreshTokenExpiration() + this.authenticationParams.getLatencyInMs()));

        this.tokenRegistrationRepository.save(tokenRegistration);

        return Result.Success(new AuthenticationBridge(token, refreshToken));
    }


    private static Map<String,String> StaffEntityToClaimsMap(Staff staff){
        return Map.of(
                Claims.SUBJECT,staff.getUsername(),
                Claims.ID, Integer.toString(staff.getId()),
                "email",staff.getEmail(),
                "phone", staff.getAddress().getPhone()
        );
    }
}