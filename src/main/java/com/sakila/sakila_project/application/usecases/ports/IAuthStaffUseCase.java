package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.custom.authentication.*;

public interface IAuthStaffUseCase {
    public AuthenticationBridge Authenticate(AuthenticationCredentials credentials, AuthenticationParams params);
    public AuthenticationBridge Authenticate(AuthenticationBridge authenticationRequest, AuthenticationParams params);
    public AuthenticatedUserMetadata GetMetadata(String token);
}
