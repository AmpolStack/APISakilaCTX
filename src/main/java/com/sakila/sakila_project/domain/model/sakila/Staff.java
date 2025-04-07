package com.sakila.sakila_project.domain.model.sakila;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private int id;
    @Column(length = 45)
    private String first_name;
    @Column(length = 45)
    private String last_name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    private String picture;
    @Column(length = 50)
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "staff")
    private List<Payment> payments;
    private int active;
    @Column(length = 16)
    private String username;
    @Column(length = 40)
    private String password;
    private Date last_update;
}
