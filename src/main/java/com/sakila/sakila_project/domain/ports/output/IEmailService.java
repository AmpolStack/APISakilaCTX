package com.sakila.sakila_project.domain.ports.output;

import jakarta.mail.MessagingException;

import java.io.InputStream;

public interface IEmailService {
    void SendEmail(String subject, String body, String senderMail, String recipientMail) throws MessagingException;
    void SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file);
}
