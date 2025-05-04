package com.sakila.sakila_project.infrastructure.adapters.output;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class CacheAdapter implements com.sakila.sakila_project.domain.ports.output.EmailPort {

    private final JavaMailSender _emailSender;

    @Autowired
    public EmailPort(JavaMailSender mailSender) {
        this._emailSender = mailSender;
    }

    @Override
    public void SendEmail(String subject, String body, String senderMail, String recipientMail) {
        var message = new SimpleMailMessage();
        message.setFrom(senderMail);
        message.setTo(recipientMail);
        message.setSubject(subject);
        message.setText(body);
        _emailSender.send(message);
    }

    @Override
    public void SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file) {
        // TODO: Implement logic
    }
}
