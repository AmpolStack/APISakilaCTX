package com.sakila.sakila_project.application.dto;

import com.sakila.sakila_project.domain.model.sakila.Address;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExtStoreDto {
    private int id;
    private MinAddressDto address;
    private Date last_update;
}
