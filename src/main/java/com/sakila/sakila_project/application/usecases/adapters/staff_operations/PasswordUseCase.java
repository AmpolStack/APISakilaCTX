package com.sakila.sakila_project.application.usecases.adapters.staff_operations;

import com.sakila.sakila_project.application.usecases.ports.staff_operations.IPasswordUseCase;
import com.sakila.sakila_project.domain.ports.output.ICacheService;
import com.sakila.sakila_project.domain.ports.output.IEmailService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.stereotype.Service;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordUseCase implements IPasswordUseCase {

    private final StaffRepository staffRepository;
    private final ICacheService cacheService;
    private final IEmailService emailService;

    public PasswordUseCase(StaffRepository staffRepository,
                           ICacheService cacheService,
                           IEmailService emailService) {
        this.staffRepository = staffRepository;
        this.cacheService = cacheService;
        this.emailService = emailService;
    }

    @Override
    public Result<String> SendRequestForUpdatePassword(int id, String newPassword, String fromAddress) {

        if(newPassword.isBlank()){
            return Result.Failed(new Error("The new password are required", ErrorType.VALIDATION_ERROR));
        }

        var staff = this.staffRepository
                .findById(id)
                .orElse(null);

        if(staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        if(staff.getPassword().equals(newPassword.trim())){
            return Result.Failed(new Error("The password must be different from the current one", ErrorType.VALIDATION_ERROR));
        }

        var correlationId = generateRandomCode();
        var cacheExp = 5;

        var cacheResp = this.cacheService.Set(correlationId, newPassword, cacheExp, TimeUnit.MINUTES);
        if(!cacheResp.isSuccess()) {
            return Result.Failed(cacheResp.getError());
        }

        //TODO: Convert this to a template in [Resources]
        var subject = "Sakila PJT - Password Reset Code: [" + correlationId + "]";
        var body = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>"
                + "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px;\">"
                + "<h2 style=\"color: #2c3e50;\">Hi " + staff.getUsername() + ",</h2>"
                + "<p>Your password reset code is:</p>"
                + "<div style=\"background-color: #f8f9fa; padding: 15px; margin: 20px 0; text-align: center; font-size: 24px; font-weight: bold; border-radius: 5px; border: 1px solid #ddd;\">"
                + correlationId
                + "</div>"
                + "<p style=\"color: #888; font-size: 14px;\">"
                + "⏳ Code valid for " + cacheExp + " minutes</p>"
                + "</body></html>";

        var emailResp =this.emailService
                .SendEmail(subject,
                        body,
                        fromAddress,
                        staff.getEmail());

        if(!emailResp.isSuccess()) {
            return Result.Failed(emailResp.getError());
        }

        return Result.Success(correlationId);
    }

    @Override
    public Result<Void> UpdatePassword(int id, String correlationalId) {

        if(correlationalId.isBlank()){
            return Result.Failed(new Error("The correlational id is required", ErrorType.VALIDATION_ERROR));
        }

        var resultCache = this.cacheService.Get(correlationalId);
        if(!resultCache.isSuccess()){
            return Result.Failed(resultCache.getError());
        }

        var staff = this.staffRepository
                .findById(id)
                .orElse(null);

        if(staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        staff.setPassword(resultCache.getData());
        this.staffRepository.save(staff);
        return Result.Success();
    }

    public static String generateRandomCode() {
        // Generate number between [0,999.999]
        int number = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        // Formats the number, if 'number' no have 6 digits, fills with zero
        // example ( number = 123 ), returns ( 000123 )
        return String.format("%06d", number);
    }
}
