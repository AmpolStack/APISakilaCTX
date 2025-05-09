package com.sakila.sakila_project.application.usecases.ports;

import jakarta.mail.MessagingException;

public interface IPasswordUseCase {
    String SendRequestForUpdatePassword(int id, String newPassword, String fromAddress) throws MessagingException;
    void UpdatePassword(int id, String correlationalId);
}
