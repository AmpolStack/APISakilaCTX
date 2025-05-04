package com.sakila.sakila_project.domain.ports.output;

import java.util.concurrent.TimeUnit;

public interface ICacheService {
    public void Set(String key, Object value, int minutes);
    public void Set(String key, Object value, int time, TimeUnit timeUnit);
    public String Get(String key);
    public String Del(String key);
}
