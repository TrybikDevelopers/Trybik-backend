package org.pkwmtt.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.timetable.TimetableCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import test.TestConfig;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@ActiveProfiles("test")
class CacheConfigTest extends TestConfig {
    
    @Autowired
    private TimetableCacheService service;
    
    @Autowired
    private CacheManager cacheManager;
    
    @BeforeEach
    public void initWireMock () {
        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/plany/o25.html")).willReturn(aResponse()
                                                                                               .withStatus(200)
                                                                                               .withHeader("Content-Type", "text/*")
                                                                                               .withBody(ValuesForTest.timetableHTML)));
        
        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/lista.html")).willReturn(aResponse()
                                                                                           .withStatus(200)
                                                                                           .withHeader("Content-Type", "text/*")
                                                                                           .withBody(ValuesForTest.listHTML)));
    }
    
    @Test
    void testCacheKeyPresent_Schedule () throws JsonProcessingException {
        //given
        
        //when
        service.getGeneralGroupSchedule("12K1");
        var cache = cacheManager.getCache("timetables");
        
        //then
        assertAll(
          () -> {
              assertThat(cache).isNotNull();
              assertThat(cache.get("generalGroupMap", String.class)).isEqualTo(
                "{\"11K2\":\"plany/o8.html\",\"12K1\":\"plany/o25.html\",\"11A1\":\"plany/o1.html\",\"12K3\":\"plany/o27.html\",\"12K2\":\"plany/o26.html\"}");
          }, () -> {
              var wrapper = cache.get("timetable_12K1");
              assertThat(wrapper).isNotNull();
              assertThat(wrapper.get()).isInstanceOf(String.class);
          }
        );
    }
    
    @Test
    @Disabled("hard coded hours")
    void testCacheKeyPresent_HoursList () {
        //given
        
        //when
        service.getListOfHours();
        var cache = cacheManager.getCache("timetables");
        
        //then
        assertAll(
          () -> {
              assertThat(cache).isNotNull();
              assertThat(cache.get("hourList", String.class)).isEqualTo(
                "[\"7:30- 8:15\",\"8:15- 9:00\",\"9:15-10:00\",\"10:00-10:45\",\"11:00-11:45\",\"11:45-12:30\",\"12:45-13:30\",\"13:30-14:15\",\"14:30-15:15\",\"15:15-16:00\",\"16:15-17:00\",\"17:00-17:45\",\"18:00-18:45\",\"18:45-19:30\",\"19:45-20:30\",\"20:30-21:15\"]");
          }, () -> {
              var wrapper = cache.get("hourList");
              assertThat(wrapper).isNotNull();
              assertThat(wrapper.get()).isInstanceOf(String.class);
          }
        );
    }
}