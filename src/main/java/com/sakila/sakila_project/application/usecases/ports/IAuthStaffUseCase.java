package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.custom.authentication.*;

public interface IAuthStaffUseCase {
    public AuthenticationResponse Authenticate(AuthenticationCredentials credentials, AuthenticationParams params);
    public AuthenticationResponse Authenticate(AuthenticationRequest authenticationRequest, AuthenticationParams params);
    public AuthenticatedUserMetadata GetMetadata(String token);
}
