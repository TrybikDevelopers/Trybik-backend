package org.pkwmtt.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.InternalException;
import org.pkwmtt.calendar.exams.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.exceptions.MissingHeaderException;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Component
public class AdminRequestInterceptor implements HandlerInterceptor {

    private static final String X_ADMIN_KEY_HEADER = "X-ADMIN-KEY";

    private final ApiKeyService apiKeyService;
    
    @Override
    public boolean preHandle (@NonNull HttpServletRequest request,
                              @NonNull HttpServletResponse response,
                              @NonNull Object handler) throws MissingHeaderException {
        try {
            String providedApiKey = request.getHeader(X_ADMIN_KEY_HEADER);
            
            if (isNull(providedApiKey) || providedApiKey.isBlank()) {
                throw new MissingHeaderException(X_ADMIN_KEY_HEADER);
            }
            
            apiKeyService.validateApiKey(providedApiKey, Role.ADMIN);
        } catch (MissingHeaderException e) {
            throw new MissingHeaderException(X_ADMIN_KEY_HEADER);
        } catch (IncorrectApiKeyValue e) {
            throw new IncorrectApiKeyValue();
        } catch (Exception e) {
            throw new InternalException("Internal server error while validating API key.");
        }
        return true;
    }
    
}
