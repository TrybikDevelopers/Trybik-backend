package org.pkwmtt.utils;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Slf4j
public class UtilsService {
    
    private final UtilsRepository repository;
    private final Cache cache;
    
    @Autowired
    public UtilsService (UtilsRepository repository, CacheManager cacheManager) {
        this.repository = repository;
        this.cache = cacheManager.getCache("utils");
    }
    
    public Optional<LocalDate> getEndOfSemester () {
        String key = "endOfSemester";
        log.debug("Loading endOfSemester from cache/DB");
        
        // Load string value from cache or DB if missing
        String val = cache.get(key, () -> repository.findByKey(key).map(UtilsProperty::getValue).orElse(null));
        
        if (val == null) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(LocalDate.parse(val));
        } catch (Exception ex) {
            // corrupted data -> evict cache entry so next read will reload from DB (and log)
            cache.evict(key);
            log.warn("Failed to parse endOfSemester value='{}'", val, ex);
            return Optional.empty();
        }
    }
    
    @Transactional
    public LocalDate setEndOfSemester (LocalDate date) {
        String key = "endOfSemester";
        UtilsProperty prop = repository.findByKey(key)
          .orElseGet(() -> new UtilsProperty(key, null, "date"));
        prop.setValue(date.toString());
        prop.setType("date");
        repository.save(prop);
        
        // update cache so readers get fresh value
        if (cache != null) {
            cache.put(key, date.toString());
        }
        
        log.info("endOfSemester set to {}", date);
        return date;
    }
    
    @Transactional
    public void removeEndOfSemester () {
        String key = "endOfSemester";
        repository.findByKey(key).ifPresent(repository::delete);
        if (cache != null) {
            cache.evict(key);
        }
        log.info("endOfSemester removed from DB and cache evicted");
    }
}
