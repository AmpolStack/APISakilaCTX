package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.domain.results.Result;

public interface IMutableStaffUseCase {
    Result<BaseStaffDto> updateAllStaffProperties(BaseStaffDto baseStaffDto, int staffId);
    Result<ExtendedStaffDto> updateAddresses(BaseAddressDto address, int staffId6);
    Result<Void> updateAssignedStore(int storeId, int staffId);
}
