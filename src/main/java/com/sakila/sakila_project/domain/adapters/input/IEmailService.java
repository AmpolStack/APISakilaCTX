package com.sakila.sakila_project.domain.adapters.input;

import java.io.InputStream;

public interface IEmailService {
    void SendEmail(String subject, String body, String senderMail, String recipientMail);
    void SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file);
}
