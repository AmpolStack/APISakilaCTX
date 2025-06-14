package com.sakila.sakila_project.application.dto.staff;

import com.sakila.sakila_project.application.dto.others.AddressDto;
import com.sakila.sakila_project.application.dto.others.StoreDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffWithStoreAndAddressDto extends StaffDto {
    private StoreDto store;
    private AddressDto address;
}
