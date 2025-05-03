package com.sakila.sakila_project.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfiguration {
    @Bean(name = "primaryRedisStandalone")
    @Primary
    @ConfigurationProperties(prefix = "redis.primary")
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        return new RedisStandaloneConfiguration();
    }

    @Bean(name = "primaryRedisFactory")
    @Primary
    public RedisConnectionFactory redisConnectionFactory(
            @Qualifier("primaryRedisStandalone") RedisStandaloneConfiguration redisStandaloneConfiguration
    ) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean(name = "primaryRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> primaryRedisTemplate(
            @Qualifier("primaryRedisFactory") RedisConnectionFactory redisConnectionFactory
    ){
        RedisTemplate<String,Object> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(redisConnectionFactory);
        return tpl;
    }

}
