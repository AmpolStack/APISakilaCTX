package com.sakila.sakila_project.application.usecases;

import com.sakila.sakila_project.domain.adapters.input.IEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@Slf4j
public class EmailService implements IEmailService {

    private final JavaMailSender _emailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this._emailSender = mailSender;
    }

    @Override
    public boolean SendEmail(String subject, String body, String senderMail, String recipientMail) {
        var message = new SimpleMailMessage();
        message.setFrom(senderMail);
        message.setTo(recipientMail);
        message.setSubject(subject);
        message.setText(body);
        try{
            _emailSender.send(message);
            return true;
        }
        catch(Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file) {
        return false;
    }
}
