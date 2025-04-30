package com.sakila.sakila_project.application.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtStaffDto extends MinStaffDto{
    private ExtStoreDto store;
    private MinAddressDto address;
}
