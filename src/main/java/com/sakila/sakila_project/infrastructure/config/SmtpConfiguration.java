package com.sakila.sakila_project.infrastructure.config;

import com.sakila.sakila_project.application.custom.SmtpOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class SmtpConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "email")
    public SmtpOptions smtpOptions(){ return new SmtpOptions();}

    @Bean
    public JavaMailSender getJavaMailSender(SmtpOptions smtpOptions) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpOptions.getHost());
        mailSender.setPort(smtpOptions.getPort());
        mailSender.setUsername(smtpOptions.getUsername());
        mailSender.setPassword(smtpOptions.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", smtpOptions.getTransportProtocol());
        props.put("mail.smtp.auth", smtpOptions.isSmtpAuth());
        props.put("mail.debug", smtpOptions.isDebug());
        props.put("mail.smtp.starttls.enable", smtpOptions.isTlsEnabled());
        return mailSender;
    }

}
