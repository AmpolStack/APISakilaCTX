package com.sakila.sakila_project.application.usecases.adapters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.IGetStaffUseCase;
import com.sakila.sakila_project.domain.model.sakila.Staff;
import com.sakila.sakila_project.domain.ports.output.ICacheService;
import com.sakila.sakila_project.domain.ports.output.repositories.sakila.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
public class GetStaffUseCase implements IGetStaffUseCase {

    private final StaffRepository _staffRepository;
    private final BaseDtoMapper _baseDtoMapper;
    private final StaffDtoMapper _staffDtoMapper;
    private final ICacheService _cacheService;

    public GetStaffUseCase(StaffRepository staffRepository,
                           BaseDtoMapper baseDtoMapper,
                           StaffDtoMapper staffDtoMapper,
                           ICacheService cacheService) {
        _staffRepository = staffRepository;
        _baseDtoMapper = baseDtoMapper;
        _staffDtoMapper = staffDtoMapper;
        _cacheService = cacheService;
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
        var inCache = _cacheService.Get(Integer.toString(id));
        var serializer = new ObjectMapper();

        if(inCache!=null){

            try {
                return serializer.readValue(inCache, Staff.class);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to deserialize staff", e);
            }
        }
        else{
            var staffOp = _staffRepository.findById(id);
            var staff = staffOp.orElseThrow(() -> new NoSuchElementException("No staff found with this id"));
            try {
                var staffJson = serializer.writeValueAsString(staff);
                _cacheService.Set(Integer.toString(id), staffJson, 1, TimeUnit.MINUTES);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize staff", e);
            }

            return staff;

        }
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
