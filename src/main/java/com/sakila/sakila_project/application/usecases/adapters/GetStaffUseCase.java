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
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
    public Result<ExtendedStaffDto> WithCompleteInfo(int id) {
        return getStaff(id, _staffDtoMapper::toDto, ExtendedStaffDto.class );
    }

    @Override
    public Result<BaseStaffDto> WithBasicInfo(int id) {
        return getStaff(id, _baseDtoMapper::toMinStaffDto, BaseStaffDto.class);
    }


    private <T> Result<T> getStaff(int id, Function<Staff, T> mapFunction, Class<T> clazz){

        var inCacheResult = _cacheService.Get(Integer.toString(id));
        if(!inCacheResult.isSuccess()){
            return Result.Failed(inCacheResult.getError());
        }

        var inCache = inCacheResult.getData();
        var serializer = new ObjectMapper();

        if(inCache==null){
            var map = _staffRepository
                    .findByIdWithStoreAndAddress(id)
                    .map(mapFunction)
                    .orElse(null);

            if(map==null){
                return Result.Failed(new Error("Staff not found with this id", ErrorType.NOT_FOUND_ERROR));
            }

            String staffJson;

            try {
                staffJson = serializer.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                return Result.Failed(new Error("Failed to serialize staff", ErrorType.OPERATION_ERROR));
            }

            var setCacheResp = _cacheService.Set(Integer.toString(id), staffJson, 1, TimeUnit.MINUTES);

            if(!setCacheResp.isSuccess()){
                return Result.Failed(setCacheResp.getError());
            }

            return Result.Success(map);
        }
        else{

            try {
                var serialization = serializer.readValue(inCache, clazz);
                return Result.Success(serialization);
            } catch (JsonProcessingException e) {
                return Result.Failed(new Error("Failed to deserialize staff", ErrorType.OPERATION_ERROR));
            }

        }
    }

    @Override
    public Result<List<BaseStaffDto>> AllWithBasicInfo() {
        var staffList = getStaffList();
        return Result.Success(_baseDtoMapper.toMinStaffDtoList(staffList));
    }

    @Override
    public Result<List<ExtendedStaffDto>> AllWithCompleteInfo() {
        var staffList = getStaffList();
        return Result.Success(_staffDtoMapper.toListDto(staffList));
    }

    private List<Staff> getStaffList(){
        return _staffRepository.findAll();
    }
}
