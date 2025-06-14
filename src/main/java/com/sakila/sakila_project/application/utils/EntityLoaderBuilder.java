package com.sakila.sakila_project.application.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sakila.sakila_project.domain.ports.output.services.ICacheService;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class EntityLoaderBuilder<TModel, TDto> {

    private ICacheService cacheService;
    private final ObjectMapper jsonMapper = new ObjectMapper();

    private Function<Integer, Optional<TModel>> modelSupplier = null;
    private Function<TModel, TDto> mappingMethod = null;
    private Function<TDto, String> cacheIdentifierProperty = null;

    public EntityLoaderBuilder() {
        Clear();
    }

    public EntityLoaderBuilder<TModel, TDto> setCacheService(ICacheService cacheService) {
        this.cacheService = cacheService;
        return this;
    }

    public EntityLoaderBuilder<TModel, TDto> setEntity(Function<Integer, Optional<TModel>> supplier){
        this.modelSupplier = supplier;
        return this;
    }

    public EntityLoaderBuilder<TModel, TDto> setMappingMethod(Function<TModel, TDto> mappingMethod){
        this.mappingMethod = mappingMethod;
        return this;
    }

    public EntityLoaderBuilder<TModel, TDto> setCacheIdentifierWithDto(Function<TDto, String> cacheIdentifierProperty){
        this.cacheIdentifierProperty = cacheIdentifierProperty;
        return this;
    }

    public void Clear() {
        this.modelSupplier = null;
        this.cacheIdentifierProperty = null;
        this.mappingMethod = null;
    }

    public Result<TDto> buildWithReferenceId(String referenceId, Class<TModel> modelClazz, Class<TDto> dtoClazz) {
        var idResult = IdTransformer.obtainId(referenceId);

        if(!idResult.isSuccess()){
            return Result.Failed(idResult);
        }

        var id = idResult.getData();
        return buildWithId(id, modelClazz, dtoClazz);
    }

    public Result<TDto> buildWithId(int id, Class<TModel> modelClazz, Class<TDto> dtoClazz) {
        if(this.modelSupplier == null || this.mappingMethod == null || this.cacheIdentifierProperty == null){
            return Result.Failed(new Error("Internal Error", ErrorType.OPERATION_ERROR));
        }

        var inCacheResult = this.cacheService.Get(Integer.toString(id));

        if(!inCacheResult.isSuccess()){
            var model =  this.modelSupplier
                        .apply(id)
                        .map(this.mappingMethod)
                        .orElse(null);

            if(model == null){
                var name = modelClazz.getSimpleName();
                return Result.Failed(new Error(name + " not found with this id", ErrorType.NOT_FOUND_ERROR));
            }

            String jsonModel;

            try {
                jsonModel = this.jsonMapper.writeValueAsString(model);
            }
            catch(JsonProcessingException e){
                return Result.Failed(new Error("Failed to serialize staff", ErrorType.OPERATION_ERROR));
            }

            var identifier = this.cacheIdentifierProperty.apply(model);
            var setCacheResult = this.cacheService.Set(identifier, jsonModel, 2);

            if(setCacheResult.isSuccess()){
                return Result.Failed(setCacheResult);
            }

            return Result.Success(model);
        }
        else {
            TDto model;
            try {
                model = this.jsonMapper.readValue(inCacheResult.getData(), dtoClazz);
            }
            catch(JsonProcessingException e){
                return Result.Failed(new Error("Failed to deserialize " + modelClazz.getSimpleName(), ErrorType.PROCESSING_ERROR));
            }

            return Result.Success(model);
        }
    }
}


