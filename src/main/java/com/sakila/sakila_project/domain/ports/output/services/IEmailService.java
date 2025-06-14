package com.sakila.sakila_project.domain.ports.output.services;

import com.sakila.sakila_project.domain.results.Result;
import java.io.InputStream;

public interface IEmailService {
    Result<Void> SendEmail(String subject, String body, String senderMail, String recipientMail);
    Result<Void> SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file);
}
