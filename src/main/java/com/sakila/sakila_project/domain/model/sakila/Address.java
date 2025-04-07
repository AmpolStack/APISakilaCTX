package com.sakila.sakila_project.domain.model.sakila;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id", length = 22)
    private int id;
    @Column(length = 50, nullable = false)
    private String address;
    @Column(length = 50, nullable = true)
    private String address2;
    @Column(length = 20, nullable = false)
    private String district;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;
    @Column(length = 10, nullable = true)
    private String postal_code;
    @Column(length = 20)
    private String phone;
    private Date last_update;
}
