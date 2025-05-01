package com.sakila.sakila_project.application.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmtpOptions {
    private String Username;
    private int Port;
    private String Host;
    private String Password;
    private String TransportProtocol;
    private boolean SmtpAuth;
    private boolean TlsEnabled;
    private boolean Debug;
}
