package org.pkwmtt.studentCodes;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.pkwmtt.exceptions.StudentCodeNotFoundException;
import org.pkwmtt.exceptions.WrongStudentCodeFormatException;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.pkwmtt.studentCodes.repository.StudentCodeRepository;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.jwt.refreshToken.repository.UserRefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ActiveProfiles("database")
@SpringBootTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class StudentCodeServiceTest {
    
    @Autowired
    private StudentCodeService studentCodeService;
    
    @Autowired
    private StudentCodeRepository studentCodeRepository;
    
    @Autowired
    private UserRefreshTokenRepository userRefreshTokenRepository;
    
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("test@localhost", "test"))
      .withPerMethodLifecycle(true);
    
    @Test
    void shouldSendCorrectMailWithRepresentativePayload () {
        //given
        List<StudentCodeRequest> requests = List.of(new StudentCodeRequest("test2@localhost", "12K"));
        Pattern pattern = Pattern.compile("[A-Z0-9]{6}");
        //when
        studentCodeService.sendStudentCode(requests);
        //then
        assertAll(() -> {
            assertTrue(greenMail.waitForIncomingEmail(1));
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
            assertEquals("Kod Starosty 12K", receivedMessage.getSubject());
            assertEquals("test2@localhost", receivedMessage.getAllRecipients()[0].toString());
            Matcher matcher = pattern.matcher(Objects.requireNonNull(extractBody(receivedMessage)));
            assertTrue(matcher.find());
            String code = matcher.group(0);
            assertTrue(studentCodeRepository.existsByCode(code));
        });
    }
    
    @Test
    void shouldAggregateFailuresAndContinueProcessingOtherRequests () throws Exception {
        // given: first request is invalid (subgroup provided -> causes WrongArgumentException),
        // second request is valid and should still be processed
        List<StudentCodeRequest> requests = List.of(
          new StudentCodeRequest("bad@localhost", "12K1"),
          new StudentCodeRequest("test3@localhost", "12K")
        );
        
        Pattern pattern = Pattern.compile("[A-Z0-9]{6}");
        
        // when
        var failures = studentCodeService.sendStudentCode(requests);
        
        // then - verify failure for the bad request was collected
        assertFalse(failures.isEmpty());
        assertTrue(failures.stream().anyMatch(f -> f.superiorGroupName().contains("12K1")));
        
        // verify valid request was processed: mail received and code persisted
        assertTrue(greenMail.waitForIncomingEmail(15000,1));
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        Matcher matcher = pattern.matcher(Objects.requireNonNull(extractBody(receivedMessage)));
        assertTrue(matcher.find());
        String code = matcher.group();
        assertTrue(studentCodeRepository.existsByCode(code));
    }
    
    @Test
    void shouldAggregateMultipleFailuresIntoSingleExceptionMessage () {
        // given: both requests invalid (subgroups provided)
        List<StudentCodeRequest> requests = List.of(
          new StudentCodeRequest("a@localhost", "12K1"),
          new StudentCodeRequest("b@localhost", "34L2")
        );
        
        // when
        var failures = studentCodeService.sendStudentCode(requests);

        // then - verify both failures were collected and contain group names and exception info
        assertNotNull(failures);
        assertEquals(2, failures.size(), "Expected two failures collected");

        assertTrue(failures.stream().anyMatch(f -> f.superiorGroupName().equals("12K1")
          && f.exceptionClass().equals("WrongArgumentException")));
        assertTrue(failures.stream().anyMatch(f -> f.superiorGroupName().equals("34L2")
          && f.exceptionClass().equals("WrongArgumentException")));
    }
    
    @Test
    void shouldGenerateTokenForRepresentative () throws Exception {
        //given
        List<StudentCodeRequest> requests = List.of(new StudentCodeRequest("test@localhost", "12K"));
        Pattern otpPattern = Pattern.compile("[A-Z0-9]{6}");
        Pattern tokenPattern = Pattern.compile("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+");
        //when
        studentCodeService.sendStudentCode(requests); //generate mail with code
        greenMail.waitForIncomingEmail(1); // fetch mail
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        Matcher otpMatcher = otpPattern.matcher(
          Objects.requireNonNull(extractBody(receivedMessage))); //get content
        
        final String code;
        if (otpMatcher.find()) {
            code = otpMatcher.group();
        } else {
            code = "";
            fail("Code not found");
        }
        
        JwtAuthenticationDto token = studentCodeService.generateTokenForUser(code); //generate token
        
        //then
        assertAll(() -> {
            assertNotNull(token);
            
            Matcher tokenMatcher = tokenPattern.matcher(token.getAccessToken());
            assertNotNull(token.getRefreshToken());
            assertTrue(tokenMatcher.find());
            assertFalse(userRefreshTokenRepository.findAll().isEmpty());
        });
    }
    
    @Test
    void shouldThrow_WrongOTPFormatException_wrongCharacters () {
        assertThrows(WrongStudentCodeFormatException.class, () -> studentCodeService.generateTokenForUser("XXXXX#"));
    }
    
    @Test
    void shouldThrow_WrongOTPFormatException_tooLongCode () {
        assertThrows(WrongStudentCodeFormatException.class, () -> studentCodeService.generateTokenForUser("X".repeat(7)));
    }
    
    @Test
    void shouldThrow_OTPCodeNotFoundException () {
        assertThrows(
          StudentCodeNotFoundException.class, () -> studentCodeService.generateTokenForUser("X".repeat(6)));
    }
    
    private String extractBody (Part part) throws Exception {
        if (part.isMimeType("text/plain") || part.isMimeType("text/html")) {
            return (String) part.getContent();
        }
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String result = extractBody(mp.getBodyPart(i));
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
}