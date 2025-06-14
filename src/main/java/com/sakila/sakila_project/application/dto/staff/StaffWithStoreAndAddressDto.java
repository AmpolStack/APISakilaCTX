package com.sakila.sakila_project.application.dto.staff;

import com.sakila.sakila_project.application.dto.others.AddressDto;
import com.sakila.sakila_project.application.dto.others.StoreDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExtendedStaffDto extends BaseStaffDto {
    private StoreDto store;
    private AddressDto address;
}
