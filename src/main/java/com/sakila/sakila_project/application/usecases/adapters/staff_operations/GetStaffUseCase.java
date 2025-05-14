package com.sakila.sakila_project.application.usecases.adapters.staff_operations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakila.sakila_project.application.dto.BaseStaffDto;
import com.sakila.sakila_project.application.dto.ExtendedStaffDto;
import com.sakila.sakila_project.application.maps.BaseDtoMapper;
import com.sakila.sakila_project.application.maps.StaffDtoMapper;
import com.sakila.sakila_project.application.usecases.ports.staff_operations.IGetStaffUseCase;
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

    private final StaffRepository staffRepository;
    private final BaseDtoMapper baseDtoMapper;
    private final StaffDtoMapper staffDtoMapper;
    private final ICacheService cacheService;

    public GetStaffUseCase(StaffRepository staffRepository,
                           BaseDtoMapper baseDtoMapper,
                           StaffDtoMapper staffDtoMapper,
                           ICacheService cacheService) {
        this.staffRepository = staffRepository;
        this.baseDtoMapper = baseDtoMapper;
        this.staffDtoMapper = staffDtoMapper;
        this.cacheService = cacheService;
    }


    @Override
    public Result<ExtendedStaffDto> WithCompleteInfo(int id) {
        return getStaff(id, this.staffDtoMapper::toDto, ExtendedStaffDto.class );
    }

    @Override
    public Result<BaseStaffDto> WithBasicInfo(int id) {
        return getStaff(id, this.baseDtoMapper::toMinStaffDto, BaseStaffDto.class);
    }


    private <T> Result<T> getStaff(int id, Function<Staff, T> mapFunction, Class<T> clazz){

        var getCacheResult = this.cacheService.Get(Integer.toString(id));
        if(!getCacheResult.isSuccess()){
            return Result.Failed(getCacheResult);
        }

        var inCache = getCacheResult.getData();
        var serializer = new ObjectMapper();

        if(inCache==null){
            var map = this.staffRepository
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

            var setCacheResult = this.cacheService.Set(Integer.toString(id), staffJson, 1, TimeUnit.MINUTES);

            if(!setCacheResult.isSuccess()){
                return Result.Failed(setCacheResult);
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
        return Result.Success(this.baseDtoMapper.toMinStaffDtoList(staffList));
    }

    @Override
    public Result<List<ExtendedStaffDto>> AllWithCompleteInfo() {
        var staffList = getStaffList();
        return Result.Success(this.staffDtoMapper.toListDto(staffList));
    }

    private List<Staff> getStaffList(){
        return this.staffRepository.findAll();
    }
}
