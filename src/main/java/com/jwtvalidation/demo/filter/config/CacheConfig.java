package com.jwtvalidation.demo.filter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${keycloak.jwks.cache-timeout-minutes}")
    private int jwksCacheTimeoutMinutes;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("jwksCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(jwksCacheTimeoutMinutes, TimeUnit.MINUTES)
                .maximumSize(100));
        return cacheManager;
    }
}
