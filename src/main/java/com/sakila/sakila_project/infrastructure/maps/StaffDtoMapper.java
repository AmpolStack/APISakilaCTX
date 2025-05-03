package com.sakila.sakila_project.infrastructure.maps;

import com.sakila.sakila_project.infrastructure.dto.ExtStaffDto;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StaffDtoMapper {
    ExtStaffDto toDto(Staff staff);
    List<ExtStaffDto> toListDto(List<Staff> staffList);
}
