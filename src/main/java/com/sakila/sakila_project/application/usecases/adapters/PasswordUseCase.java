package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.usecases.ports.IPasswordUseCase;
import com.sakila.sakila_project.domain.ports.output.ICacheService;
import com.sakila.sakila_project.domain.ports.output.IEmailService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class PasswordUseCase implements IPasswordUseCase {

    private final StaffRepository _staffRepository;
    private final ICacheService _cacheService;
    private final IEmailService _emailService;

    public PasswordUseCase(StaffRepository staffRepository,
                           ICacheService cacheService,
                           IEmailService emailService) {
        _staffRepository = staffRepository;
        _cacheService = cacheService;
        _emailService = emailService;
    }

    @Override
    public String SendRequestForUpdatePassword(int id, String newPassword, String fromAddress) throws MessagingException {
        var staff = _staffRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Staff not found"));

        var correlationId = generateRandomCode();
        var cacheExp = 5;
        _cacheService.Set(correlationId, newPassword, cacheExp, TimeUnit.MINUTES);

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

        _emailService
                .SendEmail(subject,
                        body,
                        fromAddress,
                        staff.getEmail());

        return correlationId;
    }

    @Override
    public void UpdatePassword(int id, String correlationalId) {
        var newPassword = _cacheService.Get(correlationalId);
        var staff = _staffRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Staff not found"));

        staff.setPassword(newPassword);
        _staffRepository.save(staff);
    }

    public static String generateRandomCode() {
        // Generate number between [0,999.999]
        int number = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        // Formats the number, if 'number' no have 6 digits, fills with zero
        // example ( number = 123 ), returns ( 000123 )
        return String.format("%06d", number);
    }
}
