package org.pkwmtt.security.apiKey;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.calendar.exams.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.admin.entity.AdminKey;
import org.pkwmtt.admin.repository.AdminKeyRepository;
import org.pkwmtt.security.apiKey.entity.ApiKey;
import org.pkwmtt.security.apiKey.repository.ApiKeyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    
    private final ApiKeyRepository apiKeyRepository;
    private final AdminKeyRepository adminKeyRepository;
    private final PasswordEncoder encoder;

    public String generateApiKey (String description, Role role) {
        String value = UUID.randomUUID().toString();
        saveApiKey(value, description, role);
        return value;
    }
    
    private void saveApiKey (String value, String description, Role role) {
        String encodedValue = encoder.encode(value);
        if (role == Role.ADMIN) {
            adminKeyRepository.save(new AdminKey(encodedValue, description));
        } else {
            apiKeyRepository.save(new ApiKey(encodedValue, description));
        }
    }

    public void validateApiKey (String value, Role role) throws IncorrectApiKeyValue {
        if (isNull(value) || value.trim().isEmpty()) {
            throw new IncorrectApiKeyValue();
        }
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IncorrectApiKeyValue();
        }

        if (existsInAdminKeyBase(value)) { // Admin can access all endpoint
            return;
        }
        
        if (role != Role.ADMIN && existsInPublicKeyBase(value)) {  // Normal user access
            return;
        }
        
        throw new IncorrectApiKeyValue();
    }
    
    public boolean existsInPublicKeyBase (String value) {
        return apiKeyRepository.findAll().stream()
          .map(ApiKey::getValue)
          .anyMatch(stored -> encoder.matches(value, stored));
    }
    
    public boolean existsInAdminKeyBase (String value) {
        return adminKeyRepository.findAll().stream()
          .map(AdminKey::getValue)
          .anyMatch(stored -> encoder.matches(value, stored));
    }
    
    public Map<String, String> getMapOfPublicApiKeys () {
        Map<String, String> objectMap = new HashMap<>();
        
        apiKeyRepository.findAll().forEach(item -> objectMap.put(item.getValue(), item.getDescription()));
        
        return objectMap;
    }
    
}
