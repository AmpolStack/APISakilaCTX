package com.sakila.sakila_project.application.usecases.ports.staff_operations;

import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IGetStaffUseCase {
    Result<ExtendedStaffDto> WithCompleteInfo(int id);
    Result<List<ExtendedStaffDto>> AllWithCompleteInfo();
    Result<BaseStaffDto> WithBasicInfo(int id);
    Result<List<BaseStaffDto>> AllWithBasicInfo();
}
