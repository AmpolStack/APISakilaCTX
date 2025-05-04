package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.custom.authentication.*;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import com.sakila.sakila_project.domain.ports.input.IJwtService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.tokens.TokenRegistrationRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthStaffUseCase implements IAuthStaffUseCase {

    private final AuthenticationParams _authenticationParams;
    private final TokenRegistrationRepository _tokenRegistrationRepository;
    private final IJwtService _jwtService;
    private final StaffRepository _staffRepository;

    @Autowired
    public AuthStaffUseCase(IJwtService jwtService,
                            TokenRegistrationRepository tokenRegistrationRepository,
                            StaffRepository staffRepository,
                            AuthenticationParams authenticationParams) {
        _jwtService = jwtService;
        _tokenRegistrationRepository = tokenRegistrationRepository;
        _staffRepository = staffRepository;
        _authenticationParams = authenticationParams;
    }

    @Override
    public AuthenticationResponse Authenticate(AuthenticationCredentials credentials, AuthenticationParams params) {
        var staffOp = _staffRepository.findStaffByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        return SaveRegistry(staffOp, params);
    }

    @Override
    public AuthenticationResponse Authenticate(AuthenticationRequest authenticationRequest, AuthenticationParams params) {

        Optional<TokenRegistration> registrationOp;
        try{
            registrationOp = _tokenRegistrationRepository.findTokenRegistrationByTokenAndRefreshToken(
                    authenticationRequest.getToken(),
                    authenticationRequest.getRefreshToken());
        }
        catch(Exception e){
            return new AuthenticationResponse(
                    "Internal server error",
                    Boolean.FALSE,
                    500
            );
        }


        if(registrationOp.isEmpty()){
            return new AuthenticationResponse(
                    "Token not foud",
                    Boolean.FALSE);
        }

        var registration = registrationOp.get();
        Optional<Staff> staffOp;
        try{
            staffOp = _staffRepository.findById(registration.getIdUser());
        }
        catch(Exception e){
            return new AuthenticationResponse("Internal server error", Boolean.FALSE, 500);
        }

        return SaveRegistry(staffOp, params);
    }

    @Override
    public AuthenticatedUserMetadata GetMetadata(String token) {
        var claims = this._jwtService.GetAllClaims(token, _authenticationParams.getSecret());
        var authenticatedUserMetadata = new AuthenticatedUserMetadata();
        authenticatedUserMetadata.setUsername(claims.getSubject());
        authenticatedUserMetadata.setId(Integer.parseInt(claims.getId()));
        authenticatedUserMetadata.setEmail(claims.get("email", String.class));
        authenticatedUserMetadata.setPhone(claims.get("phone", String.class));
        return authenticatedUserMetadata;
    }


    private AuthenticationResponse SaveRegistry(Optional<Staff> staffOp, AuthenticationParams params) {

        if (staffOp.isEmpty()) {
            return new AuthenticationResponse(
                    "User not found",
                    Boolean.FALSE);
        }

        var staff = staffOp.get();
        var claims = StaffEntityToClaimsMap(staff);
        String token = "";

        try{
            token = _jwtService.GenerateToken(claims, params.getTokenExpiration(), params.getSecret());
        }
        catch(Exception e){
            return new AuthenticationResponse(
                    "Token generation is failed",
                    Boolean.FALSE);
        }

        var refreshToken = _jwtService.GenerateRefreshToken();

        var tokenRegistration = new TokenRegistration();
        tokenRegistration.setToken(token);
        tokenRegistration.setRefreshToken(refreshToken);
        tokenRegistration.setActive(true);
        tokenRegistration.setIdUser(staff.getId());
        tokenRegistration.setCreationDate(new Date());
        tokenRegistration.setExpirationDate(new Date(new Date().getTime() + params.getTokenExpiration() + _authenticationParams.getLatencyInMs()));

        try{
            _tokenRegistrationRepository.save(tokenRegistration);
        }
        catch(Exception e){
            return new AuthenticationResponse(
                    "Token registration failed",
                    Boolean.FALSE);
        }

        return new AuthenticationResponse(
                "Successfully authenticated",
                Boolean.TRUE,
                token,
                refreshToken);
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