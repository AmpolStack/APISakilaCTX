package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.dto.BaseAddressDto;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.IMutableStaffUseCase;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
public class MutableStaffUseCase implements IMutableStaffUseCase {

    private final StaffRepository _staffRepository;
    private final BaseDtoMapper _baseDtoMapper;
    private final StaffDtoMapper _staffDtoMapper;

    @Autowired
    public MutableStaffUseCase(StaffRepository staffRepository,
                               BaseDtoMapper baseDtoMapper,
                               StaffDtoMapper staffDtoMapper) {
        _staffRepository = staffRepository;
        _baseDtoMapper = baseDtoMapper;
        _staffDtoMapper = staffDtoMapper;
    }


    //TODO: CHANGE THE LOGIC
    @Override
    @Transactional
    public BaseStaffDto updateAllStaffProperties(BaseStaffDto baseStaffDto, int staffId) {

        var staff = _staffRepository.findById(staffId)
                .orElseThrow(NoSuchElementException::new);

        var staffMap = _baseDtoMapper.toStaff(baseStaffDto);

        staff.setFirst_name(staffMap.getFirst_name());
        staff.setLast_name(staffMap.getLast_name());
        staff.setEmail(staffMap.getEmail());
        staff.setPicture(staffMap.getPicture());
        staff.setActive(staffMap.getActive());
        staff.setUsername(staffMap.getUsername());
        staff.setLast_update(staffMap.getLast_update());

        var resp = _staffRepository.save(staff);

        return _baseDtoMapper.toMinStaffDto(resp);
    }


    //TODO: CHANGES THE LOGIC
    @Override
    @Transactional
    public ExtendedStaffDto updateAddresses(BaseAddressDto addressDto, int staffId) {

        var staff = _staffRepository.findByIdWithAddress(staffId)
                .orElseThrow(() -> new NoSuchElementException("Staff does not exist"));

        var addressOp = staff.getAddress();
        var address = _baseDtoMapper.toAddress(addressDto);

        addressOp.setAddress(address.getAddress());
        addressOp.setAddress2(address.getAddress2());
        addressOp.setDistrict(address.getDistrict());
        addressOp.setPostal_code(address.getPostal_code());
        addressOp.setLast_update(address.getLast_update());
        addressOp.setPhone(address.getPhone());

        var resp = _staffRepository.save(staff);

        return _staffDtoMapper.toDto(resp);
    }

    @Override
    public void updateAssignedStore(int storeId, int staffId) {
        var staff = _staffRepository.findByIdWithStoreAndAddress(staffId)
        .orElseThrow(() -> new NoSuchElementException("Staff does not exist"));

        staff.getStore().setId(storeId);
        _staffRepository.save(staff);
    }

}
