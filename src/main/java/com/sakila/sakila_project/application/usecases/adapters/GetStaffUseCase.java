package com.sakila.sakila_project.application.usecases.adapters;

import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.IGetStaffUseCase;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GetStaffUseCase implements IGetStaffUseCase {

    private final StaffRepository _staffRepository;
    private final BaseDtoMapper _baseDtoMapper;
    private final StaffDtoMapper _staffDtoMapper;

    public GetStaffUseCase(StaffRepository staffRepository,
                           BaseDtoMapper baseDtoMapper,
                           StaffDtoMapper staffDtoMapper) {
        _staffRepository = staffRepository;
        _baseDtoMapper = baseDtoMapper;
        _staffDtoMapper = staffDtoMapper;
    }


    @Override
    public ExtendedStaffDto WithCompleteInfo(int id) {
        var staff = getStaff(id);
        return _staffDtoMapper.toDto(staff);
    }

    @Override
    public BaseStaffDto WithBasicInfo(int id) {
        var staff = getStaff(id);
        return _baseDtoMapper.toMinStaffDto(staff);
    }

    private Staff getStaff(int id){
        var staffOp = _staffRepository.findById(id);
        return staffOp.orElseThrow(() -> new NoSuchElementException("No staff found with this id"));
    }

    @Override
    public List<BaseStaffDto> AllWithBasicInfo() {
        var staffList = getStaffList();
        return _baseDtoMapper.toMinStaffDtoList(staffList);
    }

    @Override
    public List<ExtendedStaffDto> AllWithCompleteInfo() {
        var staffList = getStaffList();
        return _staffDtoMapper.toListDto(staffList);
    }

    private List<Staff> getStaffList(){
        return _staffRepository.findAll();
    }
}
