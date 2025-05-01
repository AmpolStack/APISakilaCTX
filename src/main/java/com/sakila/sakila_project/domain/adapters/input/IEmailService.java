package com.sakila.sakila_project.domain.adapters.input;

import java.io.InputStream;

public interface IEmailService {
    boolean SendEmail(String subject, String body, String recipientMail);
    boolean SendEmail(String subject, String body, String recipientMail, InputStream file);
}
