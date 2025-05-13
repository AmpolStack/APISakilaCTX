package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.IMutableStaffUseCase;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MutableStaffUseCase implements IMutableStaffUseCase {

    private final StaffRepository staffRepository;
    private final BaseDtoMapper baseDtoMapper;
    private final StaffDtoMapper staffDtoMapper;

    @Autowired
    public MutableStaffUseCase(StaffRepository staffRepository,
                               BaseDtoMapper baseDtoMapper,
                               StaffDtoMapper staffDtoMapper) {
        this.staffRepository = staffRepository;
        this.baseDtoMapper = baseDtoMapper;
        this.staffDtoMapper = staffDtoMapper;
    }


    //TODO: CHANGE THE LOGIC
    @Override
    @Transactional
    public Result<BaseStaffDto> updateAllStaffProperties(BaseStaffDto baseStaffDto, int staffId) {

        var staff = this.staffRepository.findById(staffId)
                .orElse(null);

        if (staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        var staffMap = this.baseDtoMapper.toStaff(baseStaffDto);

        staff.setFirst_name(staffMap.getFirst_name());
        staff.setLast_name(staffMap.getLast_name());
        staff.setEmail(staffMap.getEmail());
        staff.setPicture(staffMap.getPicture());
        staff.setActive(staffMap.getActive());
        staff.setUsername(staffMap.getUsername());
        staff.setLast_update(staffMap.getLast_update());

        var verify = staff.Verify();

        if(!verify.isSuccess()){
            return Result.Failed(verify.getError());
        }

        var resp = this.staffRepository.save(staff);

        return Result.Success(this.baseDtoMapper.toMinStaffDto(resp));
    }


    //TODO: CHANGES THE LOGIC
    @Override
    @Transactional
    public Result<ExtendedStaffDto> updateAddresses(BaseAddressDto addressDto, int staffId) {

        var staff = this.staffRepository.findByIdWithAddress(staffId)
                .orElse(null);

        if (staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        var addressResp = staff.getAddress();

        if(!addressResp.Verify().isSuccess()){
            return Result.Failed(addressResp.Verify());
        }

        var address = this.baseDtoMapper.toAddress(addressDto);

        addressResp.setAddress(address.getAddress());
        addressResp.setAddress2(address.getAddress2());
        addressResp.setDistrict(address.getDistrict());
        addressResp.setPostal_code(address.getPostal_code());
        addressResp.setLast_update(address.getLast_update());
        addressResp.setPhone(address.getPhone());

        var resp = this.staffRepository.save(staff);

        return Result.Success(this.staffDtoMapper.toDto(resp));
    }

    @Override
    public Result<Void> updateAssignedStore(int storeId, int staffId) {
        var staff = this.staffRepository.findByIdWithStoreAndAddress(staffId)
        .orElse(null);

        if (staff == null) {
            return Result.Failed(new Error("No exist staff with this id", ErrorType.NOT_FOUND_ERROR));
        }

        staff.getStore().setId(storeId);
        this.staffRepository.save(staff);
        return Result.Success();
    }

}
