package com.sakila.sakila_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Generated;
import java.util.Date;

@Getter
@Setter
@Entity
public class actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "actor_id")
    private int id;
    private String first_name;
    private String last_name;
    private Date last_update;
}
