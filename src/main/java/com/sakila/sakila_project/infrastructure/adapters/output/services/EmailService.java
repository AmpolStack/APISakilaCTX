package com.sakila.sakila_project.infrastructure.adapters.output.services;

import com.sakila.sakila_project.domain.ports.output.services.IEmailService;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;

@Component
public class EmailService implements IEmailService {

    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.emailSender = mailSender;
    }

    @Override
    public Result<Void> SendEmail(String subject, String body, String senderMail, String recipientMail){
        var errors = new ArrayList<String>();
        if(subject.isBlank()){
            errors.add("Subject cannot be blank");
        }
        if(body.isBlank()){
            errors.add("Body cannot be blank");
        }
        if(senderMail.isBlank()){
            errors.add("Sender mail cannot be blank");
        }
        if(recipientMail.isBlank()){
            errors.add("Recipient mail cannot be blank");
        }

        if(!errors.isEmpty()){
            return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));
        }

        var message = this.emailSender.createMimeMessage();
        try{
            var helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(senderMail);
            helper.setTo(recipientMail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = isHtml
            this.emailSender.send(message);
        }
        catch (MessagingException | MailException ex){
            return Result.Failed(new Error("Error sending email", ErrorType.OPERATION_ERROR));
        }

        return Result.Success();
    }


    @Override
    public Result<Void> SendEmail(String subject, String body, String senderMail, String recipientMail, InputStream file) {
        // TODO: Implement logic
        return Result.Success();
    }
}
