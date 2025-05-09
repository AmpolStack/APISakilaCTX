package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.custom.authentication.AuthenticationCredentials;

public interface ICredentialsUseCase {
    public String SendCorrelationId(String oldPassword, String email);
    public String UpdateCredentials(String newPassword);
    public void CheckCorrelationalId(int id, String correlationalId);
}
