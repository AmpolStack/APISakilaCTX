package com.sakila.sakila_project.infrastructure.adapters.output;

import com.sakila.sakila_project.domain.ports.output.ICacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService implements ICacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(@Qualifier("primaryRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void Set(String key, Object value, int minutes) {
        redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }

    @Override
    public void Set(String key, Object value, int time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    @Override
    public String Get(String key) {
        var resp = redisTemplate.opsForValue().get(key);
        return (String)resp;
    }

    @Override
    public String Del(String key) {
        var resp = redisTemplate.opsForValue().getAndDelete(key);
        return (String)resp;
    }

}
