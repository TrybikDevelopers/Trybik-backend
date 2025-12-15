package org.pkwmtt.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pkwmtt.cache.CacheInspector;
import org.pkwmtt.security.config.NoSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import test.TestConfig;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("database")
@ContextConfiguration(classes = NoSecurityConfig.class)
class UtilsServiceTest extends TestConfig {
    
    @Autowired
    UtilsService utilsService;
    
    @Autowired
    UtilsRepository repository;
    
    @Autowired
    CacheManager cacheManager;
    
    @Autowired
    CacheInspector cacheInspector;
    
    @BeforeEach
    void setUp () {
        // clear DB and cache before each test
        repository.deleteAll();
        var cache = cacheManager.getCache("utils");
        if (cache != null) {
            cache.clear();
        }
    }
    
    @Test
    void shouldReturnEmptyWhenMissing () {
        Optional<LocalDate> res = utilsService.getEndOfSemester();
        assertTrue(res.isEmpty(), "Expected empty Optional when endOfSemester not present");
        
        var cache = cacheManager.getCache("utils");
        assertNotNull(cache);
        assertNull(cache.get("endOfSemester", String.class));
    }
    
    @Test
    void shouldSetAndCacheEndOfSemester () {
        LocalDate date = LocalDate.of(2026, 2, 28);
        
        utilsService.setEndOfSemester(date);
        
        var prop = repository.findByKey("endOfSemester");
        assertTrue(prop.isPresent());
        assertThat(prop.get().getValue()).isEqualTo("2026-02-28");
        
        Map<Object, Object> cache = cacheInspector.getAllEntries("utils");
        assertTrue(cache.containsKey("endOfSemester"));
        assertThat(cache.get("endOfSemester")).isEqualTo("2026-02-28");
    }
    
    @Test
    void shouldRemoveEndOfSemester () {
        // first set
        LocalDate date = LocalDate.of(2026, 2, 28);
        utilsService.setEndOfSemester(date);
        
        // now remove
        utilsService.removeEndOfSemester();
        
        assertFalse(repository.findByKey("endOfSemester").isPresent());
        
        Map<Object, Object> cache = cacheInspector.getAllEntries("utils");
        assertFalse(cache.containsKey("endOfSemester"));
    }
    
    @Test
    void corruptedValueEvictsCache () {
        // insert malformed value directly into DB
        UtilsProperty bad = new UtilsProperty("endOfSemester", "not-a-date", "date");
        repository.save(bad);
        
        // ensure cache is empty
        var cache = cacheManager.getCache("utils");
        if (cache != null) {
            cache.clear();
        }
        // call getter - should attempt to parse, fail, evict and return empty
        Optional<LocalDate> res = utilsService.getEndOfSemester();
        assertTrue(res.isEmpty());
    }
}