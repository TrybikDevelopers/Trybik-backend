package org.pkwmtt.timetable;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.pkwmtt.calendar.exams.enums.SubjectType;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.timetable.dto.CustomSubjectFilterDTO;
import org.pkwmtt.timetable.dto.SubjectDTO;
import org.pkwmtt.timetable.dto.TimetableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import test.TestConfig;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TimetableControllerTest extends TestConfig {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @BeforeEach
    public void initWireMock () {
        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/plany/o25.html"))
                                            .willReturn(aResponse()
                                                          .withStatus(200)
                                                          .withHeader("Content-Type", "text/*")
                                                          .withBody(ValuesForTest.timetableHTML)));
        
        EXTERNAL_SERVICE_API_MOCK.stubFor(get(urlPathMatching("/lista.html"))
                                            .willReturn(aResponse()
                                                          .withStatus(200)
                                                          .withHeader("Content-Type", "text/*")
                                                          .withBody(ValuesForTest.listHTML)));
    }
    
    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParams () {
        //given
        var url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01",
          port
        );
        
        //when
        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);
        
        //then
        assertAll(
          () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
          () -> {
              var responseBody = response.getBody();
              assertNotNull(responseBody);
          },
          () -> {
              assertNotNull(response.getBody());
              var responseData = response.getBody().getData();
              assertEquals(5, responseData.size());
              assertEquals(14, responseData.getFirst().getOdd().size());
              assertEquals(6, responseData.getFirst().getEven().size());
          }
        );
    }
    
    @Test
    public void testGetGeneralGroupScheduleFiltered_withOptionalParamsAndCustomSubjectsForSameGeneralGroup () {
        //given
        var url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/12K1?sub=K01&sub=L01&sub=P01",
          port
        );
        List<CustomSubjectFilterDTO> payload = List.of(new CustomSubjectFilterDTO("Mechatro", "12K1", "P04"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var expectedObject = new SubjectDTO()
          .setName("Mechatro")
          .setType(SubjectType.PROJECT)
          .setClassroom("K227")
          .setRowId(2)
          .setCustom(true);
        
        //when
        
        HttpEntity<List<CustomSubjectFilterDTO>> request = new HttpEntity<>(payload, headers);
        ResponseEntity<TimetableDTO> response = restTemplate.postForEntity(
          url,
          request,
          TimetableDTO.class
        );
        //then
        assertAll(
          () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
          () -> {
              var responseBody = response.getBody();
              assertNotNull(responseBody);
          },
          () -> {
              assertNotNull(response.getBody());
              
              var responseData = response.getBody().getData();
              
              var subjects_Monday_Nr3_Odd_Row2 = responseData
                .getFirst()
                .getOdd()
                .stream()
                .filter(item -> item.getRowId() == 2)
                .toList();
              
              var subjects_Monday_Nr4_Odd_Row3 = responseData
                .getFirst()
                .getOdd()
                .stream()
                .filter(item -> item.getRowId() == 3)
                .toList();
              
              assertTrue(subjects_Monday_Nr3_Odd_Row2.contains(expectedObject));
              assertTrue(subjects_Monday_Nr4_Odd_Row3.contains(expectedObject.setRowId(3)));
              
              var subjects_Monday_Nr5_Odd_Row4 = responseData
                .getFirst()
                .getOdd()
                .stream()
                .filter(item -> item.getRowId() == 4)
                .toList();
              
              assertTrue(subjects_Monday_Nr5_Odd_Row4
                           .stream()
                           .filter(subject -> subject.getName().equals("Mechatro"))
                           .toList()
                           .isEmpty());
          }
        );
    }
    
    @Test
    public void testGetGeneralGroupScheduleFiltered_withoutParams () {
        //given
        var url = String.format("http://localhost:%s/pkwmtt/api/v1/timetables/12K1", port);
        
        //when
        ResponseEntity<TimetableDTO> response = restTemplate.getForEntity(url, TimetableDTO.class);
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        assertEquals(5, response.getBody().getData().size()); // 5 days a week
    }
    
    @Test
    public void shouldReturnListOfGeneralGroups () {
        //given
        String url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/groups/general",
          port
        );
        
        //when
        ResponseEntity<List<String>> response = restTemplate.exchange(
          url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
          }
        );
        
        //then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    public void shouldReturnListOfSubgroupsForGeneralGroup () {
        //given
        String url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/groups/12K1",
          port
        );
        
        //when
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        
        //then
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
    
    @Test
    public void shouldReturn_BadRequest_SpecifiedGeneralGroupDoesntExistsException () {
        //given
        String url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/groups/XXXX",
          port
        );
        
        //when
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
          url,
          ErrorResponseDTO.class
        );
        
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
    }
    
    @Test
    public void shouldReturn_BadRequest_SpecifiedSubGroupDoesntExistsException () {
        //given
        String url = String.format(
          "http://localhost:%s/pkwmtt/api/v1/timetables/12K1?sub=XXX",
          port
        );
        
        //when
        ResponseEntity<ErrorResponseDTO> response = restTemplate.getForEntity(
          url,
          ErrorResponseDTO.class
        );
        
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertNotNull(response.getBody());
    }
    
    @Test
    public void shouldReturn_ListOfHours () {
        //given
        String url = String.format("http://localhost:%s/pkwmtt/api/v1/timetables/hours", port);
        
        //when
        ResponseEntity<String[]> response = restTemplate.getForEntity(url, String[].class);
        
        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        String regex = "\\b(?:[0-9]|1[0-9]|2[0-3]):[0-5][0-9]-(?:[0-9]|1[0-9]|2[0-3]):[0-5][0-9]\\b";
        Pattern pattern = Pattern.compile(regex);
        Arrays.stream(response.getBody()).toList().forEach(item -> {
            Matcher matcher = pattern.matcher(item);
            if (!matcher.find()) {
                fail("Wrong hour format");
            }
        });
    }
    
    
}