package org.pkwmtt.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
@ActiveProfiles("test")
@SuppressWarnings("unused")
public class CacheInspector {
    
    private final CacheManager cacheManager;
    private final TimetableCacheService service;
    
    public Map<Object, Object> getAllEntries (String cacheName) {
        CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(cacheName);
        
        if (isNull(springCache)) {
            throw new IllegalArgumentException("No cache with name " + cacheName);
        }
        
        Cache<Object, Object> nativeCache = springCache.getNativeCache();
        
        return nativeCache.asMap();
    }
    
    public String printAllEntries (String cacheName) throws JsonProcessingException {
        service.getListOfHours();
        service.getGeneralGroupSchedule("12K1");
        service.getGeneralGroupsMap();
        var s = new StringBuilder();
        getAllEntries(cacheName).forEach((key, value) -> s
          .append("Cache[")
          .append(cacheName)
          .append("] ")
          .append(key)
          .append(" -> ")
          .append(value)
          .append("\n"));
        return s.toString();
    }
}
