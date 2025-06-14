package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private String address;
    private String address2;
    private String district;
    private String postal_code;
    private String phone;
}