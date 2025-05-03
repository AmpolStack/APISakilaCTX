package com.sakila.sakila_project.domain.ports.input;

import com.sakila.sakila_project.application.custom.AuthenticationRequest;
import com.sakila.sakila_project.application.custom.AuthenticationResponse;
import com.sakila.sakila_project.application.custom.Credentials;
import io.jsonwebtoken.Claims;
import java.util.*;

public interface IJwtService {
    AuthenticationResponse AuthenticateByRefreshToken(AuthenticationRequest authenticationRequest,
                                                      int refreshTokenExpiration,
                                                      int tokenExpiration);

    AuthenticationResponse AuthenticateByCredentials(Credentials credentials,
                                                            int refreshTokenExpiration,
                                                            int tokenExpiration);

    boolean isTokenValid(String token);

    List<Object> getClaimsList(String token,
                               Map<String,
                               Class<?>> map);

    <T> T getClaim(String token,
                   String claimName,
                   Class<T> claimType);

    Claims getAllClaims(String token);
}
