package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.staff.StaffWithStoreAndAddressDto;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffDtoMapper {
    StaffWithStoreAndAddressDto toDto(Staff staff);
    List<StaffWithStoreAndAddressDto> toListDto(List<Staff> staffList);
}
