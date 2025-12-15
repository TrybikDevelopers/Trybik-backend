package org.pkwmtt.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.calendar.exams.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.exceptions.MissingHeaderException;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test & !database") //Skip on tests
public class RequestInterceptor implements HandlerInterceptor {
    
    private static final String X_API_KEY_HEADER = "X-API-KEY";
    
    private final ApiKeyService apiKeyService;
    
    @Override
    public boolean preHandle (@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler) throws MissingHeaderException {
        String apiKey = request.getHeader(X_API_KEY_HEADER);
        
        if (isNull(apiKey) || apiKey.isBlank()) {
            apiKey = request.getHeader(X_API_KEY_HEADER.toLowerCase());
            if (isNull(apiKey) || apiKey.isBlank()) {
                throw new MissingHeaderException("X-API-KEY");
            }
        }
        
        try {
            apiKeyService.validateApiKey(apiKey, Role.REPRESENTATIVE);
        } catch (IncorrectApiKeyValue e) {
            // Rethrow specific validation error so it can be handled appropriately
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during API key validation", e);
            throw new InternalException("Internal server error with validating API key.");
        }
        
        return true;
    }
}
