package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.custom.authentication.*;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.domain.exceptions.TokenExpiredException;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.model.tokens.TokenRegistration;
import com.sakila.sakila_project.domain.ports.input.IJwtService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.domain.ports.output.repositories.tokens.TokenRegistrationRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public AuthenticationBridge Authenticate(AuthenticationCredentials credentials, AuthenticationParams params) {
        var staffOp = _staffRepository.findStaffByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        return SaveRegistry(staffOp, params);
    }

    @Override
    public AuthenticationBridge Authenticate(AuthenticationBridge authenticationRequest, AuthenticationParams params) {

        Optional<TokenRegistration> registrationOp;
        registrationOp = _tokenRegistrationRepository.findTokenRegistrationByTokenAndRefreshToken(
                    authenticationRequest.getToken(),
                    authenticationRequest.getRefreshToken());

        if(registrationOp.isEmpty()){
            throw new NoSuchElementException("The tokens are not registered");
        }

        var registration = registrationOp.get();
        if(registration.getExpirationDate().before(new Date())){
            throw new TokenExpiredException("Refresh Token has expired, please login again");
        }

        Optional<Staff> staffOp;
        staffOp = _staffRepository.findById(registration.getIdUser());

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


    private AuthenticationBridge SaveRegistry(Optional<Staff> staffOp, AuthenticationParams params) {

        if (staffOp.isEmpty()) {
            throw new NoSuchElementException("No staff registered with this id");
        }

        var staff = staffOp.get();
        var claims = StaffEntityToClaimsMap(staff);
        String token = "";

        token = _jwtService.GenerateToken(claims, params.getTokenExpiration(), params.getSecret());

        var refreshToken = _jwtService.GenerateRefreshToken();

        var tokenRegistration = new TokenRegistration();
        tokenRegistration.setToken(token);
        tokenRegistration.setRefreshToken(refreshToken);
        tokenRegistration.setActive(true);
        tokenRegistration.setIdUser(staff.getId());
        tokenRegistration.setCreationDate(new Date());
        tokenRegistration.setExpirationDate(new Date(new Date().getTime() + params.getTokenExpiration() + _authenticationParams.getLatencyInMs()));

        _tokenRegistrationRepository.save(tokenRegistration);

        return new AuthenticationBridge(
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