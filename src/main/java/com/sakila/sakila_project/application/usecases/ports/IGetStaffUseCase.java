package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;

import java.util.List;

public interface IGetStaffUseCase {
    ExtendedStaffDto WithCompleteInfo(int id);
    List<ExtendedStaffDto> AllWithCompleteInfo();
    BaseStaffDto WithBasicInfo(int id);
    List<BaseStaffDto> AllWithBasicInfo();
}
