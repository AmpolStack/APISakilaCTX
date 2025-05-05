package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendedStaffDto extends BaseStaffDto {
    private ExtendedStoreDto store;
    private BaseAddressDto address;
}
