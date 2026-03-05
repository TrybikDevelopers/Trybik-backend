package org.pkwmtt.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public Caffeine<Object, Object> caffeineConfig () {
        return Caffeine.newBuilder()
          .expireAfterWrite(5, TimeUnit.DAYS)
          .recordStats();
    }
    
    @Bean
    public CacheManager cacheManager (Caffeine<Object, Object> caffeine) {
        log.info("Initializing Caffeine Cache Manager with 5-days expiration");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // register caches used across the application so they are created upfront
        cacheManager.setCacheNames(List.of("timetables", "utils"));
        cacheManager.setCaffeine(caffeine);
        log.info("Caffeine Cache Manager initialized successfully");
        return cacheManager;
    }
    
}
