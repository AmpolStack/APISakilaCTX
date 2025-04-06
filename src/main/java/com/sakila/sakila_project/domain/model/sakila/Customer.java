package com.sakila.sakila_project.domain.model.sakila;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private int id;
    private String first_name;
    private String last_name;
    private String email;
    private boolean active;
    private Date creation_date;
    private Date last_date;
}
