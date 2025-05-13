package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.custom.authentication.*;
import com.sakila.sakila_project.domain.results.Result;

public interface IAuthStaffUseCase {
    Result<AuthenticationBridge> Authenticate(AuthenticationCredentials credentials);
    Result<AuthenticationBridge> Authenticate(AuthenticationBridge authenticationRequest);
    Result<AuthenticatedUserMetadata> GetMetadata(String token);
}
