package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.usecases.ports.ICredentialsUseCase;
import com.sakila.sakila_project.domain.ports.output.ICacheService;
import com.sakila.sakila_project.domain.ports.output.IEmailService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import org.springframework.stereotype.Service;

@Service
public class CredentialsUseCase implements ICredentialsUseCase {

    private final StaffRepository staffRepository;
    private final ICacheService cacheService;
    private final IEmailService emailService;

    public CredentialsUseCase(StaffRepository staffRepository,
                              ICacheService cacheService,
                              IEmailService emailService) {
        this.staffRepository = staffRepository;
        this.cacheService = cacheService;
        this.emailService = emailService;
    }

    @Override
    public void InitializeCredentials(int id, String password) {

    }

    @Override
    public void UpdateCredentials(String oldPassword, String newPassword) {

    }

    @Override
    public void CheckCorrelationalId(String correlationalId) {

    }
}
