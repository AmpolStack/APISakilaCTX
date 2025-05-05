package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseAddressDto {
    private String address;
    private String address2;
    private String district;
    private String postal_code;
    private String phone;
    private Date last_update;
}
