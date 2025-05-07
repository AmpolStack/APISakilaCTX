package com.sakila.sakila_project.application.maps;

import com.sakila.sakila_project.application.dto.BaseActorDto;
import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.domain.model.sakila.Actor;
import com.sakila.sakila_project.domain.model.sakila.Address;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BaseDtoMapper {

    BaseActorDto toMinimalActorDto(Actor dto);

    Staff toStaff(BaseStaffDto dto);

    Address toAddress(BaseAddressDto dto);

    List<BaseActorDto> toMinimalActorDtoList(List<Actor> dtoList);

    Actor toActor(BaseActorDto actor);

    BaseStaffDto toMinStaffDto(Staff staff);

    List<BaseStaffDto> toMinStaffDtoList(List<Staff> dtoList);
}
