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

    @ConfigurationProperties(prefix = "spring.security.jwt")
    private AuthenticationParams authenticationParams(){
        return new AuthenticationParams();
    }
    private final TokenRegistrationRepository _tokenRegistrationRepository;
    private final IJwtService _jwtService;
    private final StaffRepository _staffRepository;
    private final int latencyInMs = 5000;

    @Autowired
    public AuthStaffUseCase(IJwtService jwtService,
                            TokenRegistrationRepository tokenRegistrationRepository,
                            StaffRepository staffRepository) {
        _jwtService = jwtService;
        _tokenRegistrationRepository = tokenRegistrationRepository;
        _staffRepository = staffRepository;
    }

    @Override
    public AuthenticationResponse Authenticate(AuthenticationCredentials credentials, AuthenticationParams params) {
        var staffOp = _staffRepository.findStaffByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        return SaveRegistry(staffOp, params);
    }

    @Override
    public AuthenticationResponse Authenticate(AuthenticationRequest authenticationRequest, AuthenticationParams params) {
        var resp = new AuthenticationResponse();
        var registrationOp = _tokenRegistrationRepository.findTokenRegistrationByTokenAndRefreshToken(
                authenticationRequest.getToken(),
                authenticationRequest.getRefreshToken());

        if(registrationOp.isEmpty()){
            resp.setMessage("Token not found");
            resp.setSuccess(Boolean.FALSE);
            return resp;
        }

        var registration = registrationOp.get();
        var staffOp = _staffRepository.findById(registration.getIdUser());
        resp = SaveRegistry(staffOp, params);
        return resp;
    }

    @Override
    public AuthenticatedUserMetadata GetMetadata(String token) {
        return null;
    }


    private AuthenticationResponse SaveRegistry(Optional<Staff> staffOp, AuthenticationParams params) {

        var response = new AuthenticationResponse();
        if (staffOp.isEmpty()) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage("User not found");
            return response;
        }

        var staff = staffOp.get();
        var claims = StaffEntityToClaimsMap(staff);

        var token = _jwtService.GenerateToken(claims, params.getTokenExpiration(), params.getSecret());
        var refreshToken = _jwtService.GenerateRefreshToken();

        var tokenRegistration = new TokenRegistration();
        tokenRegistration.setToken(token);
        tokenRegistration.setRefreshToken(refreshToken);
        tokenRegistration.setActive(true);
        tokenRegistration.setIdUser(staff.getId());
        tokenRegistration.setCreationDate(new Date());
        tokenRegistration.setExpirationDate(new Date(new Date().getTime() + params.getTokenExpiration() + latencyInMs));

        _tokenRegistrationRepository.save(tokenRegistration);

        response.setSuccess(Boolean.TRUE);
        response.setMessage("Successfully authenticated");
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        return response;
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