package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffDtoMapper {
    ExtendedStaffDto toDto(Staff staff);
    List<ExtendedStaffDto> toListDto(List<Staff> staffList);
}
