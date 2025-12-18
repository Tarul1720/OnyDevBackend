package com.devcom.OnlyDev.Config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // User cache - longer TTL (30 minutes)
        cacheManager.registerCustomCache("users",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()
                        .build());

        // User profiles cache - longer TTL (30 minutes)
        cacheManager.registerCustomCache("userProfiles",
                Caffeine.newBuilder()
                        .expireAfterWrite(30, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()
                        .build());

        // Posts cache - medium TTL (5 minutes)
        cacheManager.registerCustomCache("posts",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .recordStats()
                        .build());

        // Comments cache - short TTL (2 minutes)
        cacheManager.registerCustomCache("comments",
                Caffeine.newBuilder()
                        .expireAfterWrite(2, TimeUnit.MINUTES)
                        .maximumSize(2000)
                        .recordStats()
                        .build());

        // Projects cache - medium TTL (5 minutes)
        cacheManager.registerCustomCache("projects",
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(1000)
                        .recordStats()
                        .build());

        return cacheManager;
    }
}