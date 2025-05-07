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


    @Override
    @Transactional
    public BaseStaffDto updateAllStaffProperties(BaseStaffDto baseStaffDto, int staffId) {

        if (!_staffRepository.existsById(staffId)){
            throw new NoSuchElementException("Staff does not exist");
        }

        var staff = _baseDtoMapper.toStaff(baseStaffDto);
        staff.setId(staffId);

        var resp = _staffRepository.save(staff);

        return _baseDtoMapper.toMinStaffDto(resp);
    }


    @Override
    @Transactional
    public ExtendedStaffDto updateAddresses(BaseAddressDto addressDto, int staffId) {

        var staff = _staffRepository.findByIdWithAddress(staffId)
                .orElseThrow(() -> new NoSuchElementException("Staff does not exist"));

        var address = _baseDtoMapper.toAddress(addressDto);

        address.setId(staff.getAddress().getId());

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
