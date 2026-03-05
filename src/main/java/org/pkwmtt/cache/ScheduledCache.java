package org.pkwmtt.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task that evicts configured caches every day at midnight.
 * By default this uses the server's local timezone; if you need a specific timezone
 * set the "zone" attribute on the @Scheduled annotation (for example zone = "UTC").
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledCache {
    private final CacheManager cacheManager;
    private final TimetableCacheService cacheService;
    
    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Warsaw")
    public void refreshCachesAtOneAM () throws JsonProcessingException {
        log.info("Scheduled cache refresh at 01:00 - attempting prepopulation before clearing caches");
        
        var generalGroups = cacheService.getGeneralGroupsMap().keySet();
        var toRepopulate = new java.util.ArrayList<String>();
        
        // Pre-check: ensure all groups can be fetched successfully before clearing caches.
        for (var generalGroup : generalGroups) {
            try {
                cacheService.getGeneralGroupSchedule(generalGroup);
                toRepopulate.add(generalGroup);
                log.debug("Fetched timetable for general group '{}' (pre-check)", generalGroup);
            } catch (Exception ex) {
                log.warn(
                  "Prepopulation check failed for general group '{}', aborting cache refresh", generalGroup, ex);
                return;
            }
        }
        
        // All pre-checks succeeded -> clear caches.
        for (String name : cacheManager.getCacheNames()) {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
                log.debug("Cleared cache '{}'", name);
            }
        }
        
        // Repopulate caches.
        for (var generalGroup : toRepopulate) {
            try {
                cacheService.getGeneralGroupSchedule(generalGroup);
                log.debug("Prepopulated timetable cache for general group '{}'", generalGroup);
            } catch (Exception ex) {
                log.warn("Failed to prepopulate timetable cache for general group '{}'", generalGroup, ex);
            }
        }
        log.info("Scheduled cache refresh at 01:00 completed");
    }
    
}

