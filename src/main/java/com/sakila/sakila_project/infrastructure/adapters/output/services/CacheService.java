package com.sakila.sakila_project.infrastructure.adapters.output;

import com.sakila.sakila_project.domain.ports.output.ICacheService;
import com.sakila.sakila_project.domain.results.Error;
import com.sakila.sakila_project.domain.results.ErrorType;
import com.sakila.sakila_project.domain.results.Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CacheService implements ICacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(@Qualifier("primaryRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Result<Void> Set(String key, Object value, int minutes) {
        var errors = ProveMany(key, value, minutes);

        if(!errors.isEmpty()){
            return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));
        }
        redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
        return Result.Success();
    }

    @Override
    public Result<Void> Set(String key, Object value, int time, TimeUnit timeUnit) {
        var errors = ProveMany(key, value, time);

        if(!errors.isEmpty()){
            return Result.Failed(new Error(errors, ErrorType.VALIDATION_ERROR));
        }

        redisTemplate.opsForValue().set(key, value, time, timeUnit);
        return Result.Success();
    }

    @Override
    public Result<String> Get(String key) {
        if(key.isBlank()){
            return Result.Failed(new Error("The key is required", ErrorType.VALIDATION_ERROR));
        }
        var resp = redisTemplate.opsForValue().get(key);
        return Result.Success((String)resp);
    }

    @Override
    public Result<String> Del(String key) {
        if(key.isBlank()){
            return Result.Failed(new Error("The key is required", ErrorType.VALIDATION_ERROR));
        }
        var resp = redisTemplate.opsForValue().getAndDelete(key);
        return Result.Success((String)resp);
    }

    private List<String> ProveMany(String key, Object value, int time){
        var errors = new ArrayList<String>();
        if(key.isBlank()){
            errors.add("The key is required");
        }
        if(value == null){
            errors.add("The value is required");
        }
        if(time < 0){
            errors.add("The time must be greater than 0");
        }
        return errors;
    }

}
