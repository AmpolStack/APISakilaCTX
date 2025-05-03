package com.sakila.sakila_project.domain.ports.output;

import java.io.InputStream;

public interface IEmailService {
    void SendEmail(String subject, String body, String senderMail, String recipientMail);
    void SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file);
}
