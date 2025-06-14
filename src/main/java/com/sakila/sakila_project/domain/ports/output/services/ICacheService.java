package com.sakila.sakila_project.domain.ports.output.services;

import com.sakila.sakila_project.domain.results.Result;

import java.util.concurrent.TimeUnit;

public interface ICacheService {
    Result<Void> Set(String key, Object value, int minutes);
    Result<Void> Set(String key, Object value, int time, TimeUnit timeUnit);
    Result<String> Get(String key);
    Result<String> Del(String key);
}
