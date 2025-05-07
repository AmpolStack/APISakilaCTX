package com.sakila.sakila_project.application.usecases.ports;

import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;

public interface IMutableStaffUseCase {
    BaseStaffDto updateAllStaffProperties(BaseStaffDto baseStaffDto, int staffId);
    ExtendedStaffDto updateAddresses(BaseAddressDto address, int staffId6);
    void updateAssignedStore(int storeId, int staffId);
}
