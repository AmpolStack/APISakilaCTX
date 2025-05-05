package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExtendedStoreDto {
    private int id;
    private BaseAddressDto address;
    private Date last_update;
}
