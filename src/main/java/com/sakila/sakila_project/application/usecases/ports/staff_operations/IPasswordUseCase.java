package com.sakila.sakila_project.application.usecases.ports.staff_operations;

import com.sakila.sakila_project.domain.results.Result;

public interface IPasswordUseCase {
    Result<String> SendRequestForUpdatePassword(int id, String newPassword, String fromAddress);
    Result<Void> UpdatePassword(int id, String correlationalId);
}
