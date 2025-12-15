package org.pkwmtt.timetable;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.cache.CacheInspector;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.TestConfig;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TimetableCacheServiceTest extends TestConfig {
    @Autowired
    TimetableCacheService cachedService;
    
    @Autowired
    CacheInspector cacheInspector;
    
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
    @Disabled("Values for hours are hard coded in endpoint for now")
    public void shouldHourListBePresentInCache () {
        //given
        var key = "hourList";
        cachedService.getListOfHours(); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
        
        //then
        assertAll(
          () -> assertNotNull(cacheData), () -> assertTrue(cacheData.containsKey(key)), () -> {
              var hourList = cacheData.get(key);
              assertNotNull(hourList);
              assertThat(hourList).isEqualTo(
                "[\"7:30- 8:15\",\"8:15- 9:00\",\"9:15-10:00\",\"10:00-10:45\",\"11:00-11:45\",\"11:45-12:30\",\"12:45-13:30\",\"13:30-14:15\",\"14:30-15:15\",\"15:15-16:00\",\"16:15-17:00\",\"17:00-17:45\",\"18:00-18:45\",\"18:45-19:30\",\"19:45-20:30\",\"20:30-21:15\"]");
          }
        );
    }
    
    
    @Test
    public void shouldReturnGeneralGroupsMap () throws JsonProcessingException {
        //given
        var expectedMap = Map.of(
          "11K2",
          "plany/o8.html",
          "12K1",
          "plany/o25.html",
          "11A1",
          "plany/o1.html",
          "12K3",
          "plany/o27.html",
          "12K2",
          "plany/o26.html"
        );
        
        //when
        var result = cachedService.getGeneralGroupsMap();
        
        //then
        assertThat(result).isEqualTo(expectedMap);
    }
    
    @Test
    public void shouldGeneralGroupMapBePresentInCache () throws JsonProcessingException {
        //given
        var key = "generalGroupMap";
        var expectedValue = "{\"11K2\":\"plany/o8.html\",\"12K1\":\"plany/o25.html\",\"11A1\":\"plany/o1.html\",\"12K3\":\"plany/o27.html\",\"12K2\":\"plany/o26.html\"}";
        cachedService.getGeneralGroupsMap(); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
        
        //then
        assertAll(
          () -> assertNotNull(cacheData), () -> {
              assertTrue(cacheData.containsKey(key));
              var data = cacheData.get(key);
              assertThat(data).isEqualTo(expectedValue);
          }
        );
    }
    
    @Test
    public void shouldReturn12K1Schedule () throws JsonProcessingException {
        //given
        var generalGroupName = "12K1"; // get random general group
        
        //when
        var result = cachedService.getGeneralGroupSchedule(generalGroupName);
        
        //then
        assertNotNull(result);
        assertInstanceOf(TimetableDTO.class, result);
    }
    
    @Test
    public void shouldRandomGeneralGroupScheduleBePresentInCache () throws JsonProcessingException {
        //given
        String generalGroupName = "12K1"; // get random general group
        String key = "timetable_" + generalGroupName;
        
        cachedService.getGeneralGroupSchedule(generalGroupName); // call method to save data in cache
        
        //when
        Map<Object, Object> cacheData = cacheInspector.getAllEntries("timetables"); // get all keys saved in cache
        
        //then
        assertNotNull(cacheData);
        assertTrue(cacheData.containsKey(key));
        assertInstanceOf(String.class, cacheData.get(key));
    }
}