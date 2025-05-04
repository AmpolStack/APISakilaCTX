package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.custom.authentication.*;
import com.sakila.sakila_project.application.usecases.ports.IAuthStaffUseCase;
import com.sakila.sakila_project.domain.exceptions.InvalidAuthenticationException;
import com.sakila.sakila_project.domain.exceptions.InvalidCredentialsException;
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
    public AuthenticationBridge Authenticate(AuthenticationCredentials credentials) {

        if(credentials.getUsername().isEmpty()
                || credentials.getPassword().isEmpty()
                || credentials.getUsername().isBlank()
                || credentials.getPassword().isBlank()) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        var staffOp = _staffRepository.findStaffByUsernameAndPassword(credentials.getUsername(), credentials.getPassword());
        var staff = staffOp.orElseThrow(() -> new InvalidCredentialsException("No exist staff registered with this credentials"));
        return SaveRegistry(staff);
    }

    @Override
    public AuthenticationBridge Authenticate(AuthenticationBridge authenticationRequest) {

        Optional<TokenRegistration> registrationOp;
        registrationOp = _tokenRegistrationRepository.findTokenRegistrationByTokenAndRefreshToken(
                    authenticationRequest.getToken(),
                    authenticationRequest.getRefreshToken());

        var registration = registrationOp.orElseThrow(() -> new InvalidCredentialsException("This credentials does not exist"));

        if(registration.getExpirationDate().before(new Date())){
            throw new TokenExpiredException("Refresh Token has expired, please login again");
        }

        var staffOp = _staffRepository.findById(registration.getIdUser());
        var staff = staffOp.orElseThrow(()-> new InvalidAuthenticationException("No exist staff registered with this tokens"));
        return SaveRegistry(staff);
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


    private AuthenticationBridge SaveRegistry(Staff staff) {

        var claims = StaffEntityToClaimsMap(staff);
        var token = _jwtService.GenerateToken(claims, _authenticationParams.getTokenExpiration(), _authenticationParams.getSecret());

        var refreshToken = _jwtService.GenerateRefreshToken();

        var tokenRegistration = new TokenRegistration();
        tokenRegistration.setToken(token);
        tokenRegistration.setRefreshToken(refreshToken);
        tokenRegistration.setActive(true);
        tokenRegistration.setIdUser(staff.getId());
        tokenRegistration.setCreationDate(new Date());
        tokenRegistration.setExpirationDate(new Date(new Date().getTime() + _authenticationParams.getRefreshTokenExpiration() + _authenticationParams.getLatencyInMs()));

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