package org.pkwmtt.timetable;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.ValuesForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.TestConfig;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class TimetableServiceTest extends TestConfig {

    @Autowired
    TimetableService timetableService;

    @BeforeEach
    public void setup() {
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
    public void testGetListOfCustomSubjects_filtersExcludedOnes() throws Exception {
        // when
        List<String> result = timetableService.getListOfCustomSubjects("12K1");

        // then
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected some custom subjects to be present");

        // excluded subjects should not be present
        boolean containsNiemiecki = result.stream().anyMatch(s -> s.contains("niemiecki"));
        boolean containsJAng = result.stream().anyMatch(s -> s.contains("J ang"));
        boolean containsWFHala = result.stream().anyMatch(s -> s.contains("WF hala"));

        assertFalse(containsNiemiecki, "Result should not contain 'niemiecki' subjects");
        assertFalse(containsJAng, "Result should not contain 'J ang' subjects");
        assertFalse(containsWFHala, "Result should not contain 'WF hala' subjects");

        // example of expected custom subject from the sample HTML (PKM W -> PKM)
        assertTrue(result.contains("PKM"), "Expected PKM to be present among custom subjects");
    }
}
