package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.City;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MinAddressDto {
    private String address;
    private String address2;
    private String district;
    private String postal_code;
    private String phone;
    private Date last_update;
}
