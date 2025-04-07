package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.Address;
import com.sakila.sakila_project.domain.model.sakila.Payment;
import com.sakila.sakila_project.domain.model.sakila.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class MinStaffDto {
    @Column(length = 45)
    private String first_name;
    @Column(length = 45)
    private String last_name;
    private byte[] picture;
    @Column(length = 50)
    private String email;
    private int active;
    @Column(length = 16)
    private String username;
    private Date last_update;
}
