package com.sakila.sakila_project.application.usecases.ports.staff_operations;

import com.sakila.sakila_project.application.dto.others.AddressDto;
import com.sakila.sakila_project.application.dto.staff.StaffDto;
import com.sakila.sakila_project.application.dto.staff.StaffWithStoreAndAddressDto;
import com.sakila.sakila_project.domain.results.Result;

public interface IMutableStaffUseCase {
    Result<StaffDto> updateAllStaffProperties(StaffDto baseStaffDto, int staffId);
    Result<StaffWithStoreAndAddressDto> updateAddresses(AddressDto address, int staffId6);
    Result<Void> updateAssignedStore(int storeId, int staffId);
}
