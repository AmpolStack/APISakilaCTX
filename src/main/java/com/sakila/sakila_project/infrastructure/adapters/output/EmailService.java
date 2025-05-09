package com.sakila.sakila_project.infrastructure.adapters.output;

import com.sakila.sakila_project.domain.ports.output.IEmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class EmailService implements IEmailService {

    private final JavaMailSender _emailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this._emailSender = mailSender;
    }

    @Override
    public void SendEmail(String subject, String body, String senderMail, String recipientMail) throws MessagingException {
        var message = _emailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(senderMail);
        helper.setTo(recipientMail);
        helper.setSubject(subject);
        helper.setText(body, true); // true = isHtml

        _emailSender.send(message);
    }


    @Override
    public void SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file) {
        // TODO: Implement logic
    }
}
