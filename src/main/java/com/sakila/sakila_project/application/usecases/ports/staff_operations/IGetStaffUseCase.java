package com.sakila.sakila_project.application.usecases.ports.staff_operations;

import com.sakila.sakila_project.application.dto.staff.StaffDto;
import com.sakila.sakila_project.application.dto.staff.StaffWithStoreAndAddressDto;
import com.sakila.sakila_project.domain.results.Result;

import java.util.List;

public interface IGetStaffUseCase {
    Result<StaffWithStoreAndAddressDto> WithCompleteInfo(int id);
    Result<List<StaffWithStoreAndAddressDto>> AllWithCompleteInfo();
    Result<StaffDto> WithBasicInfo(int id);
    Result<List<StaffDto>> AllWithBasicInfo();
}
