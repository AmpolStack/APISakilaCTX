package com.sakila.sakila_project.domain.model.tokens;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "history_rt")
public class TokenRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_history_rt")
    private int idHistoryRT;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "token")
    private String token;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "creation_date")
    private Date creationDate;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Column(name = "is_active")
    private boolean isActive;

}
