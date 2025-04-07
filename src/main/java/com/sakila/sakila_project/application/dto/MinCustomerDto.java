package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.Address;
import com.sakila.sakila_project.domain.model.sakila.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
public class MinCustomerDto {
    private String first_name;
    private String last_name;
    private String email;
    private boolean active;
    private Date creation_date;
    private Date last_date;
}
