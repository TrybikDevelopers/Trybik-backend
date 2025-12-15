package org.pkwmtt.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pkwmtt.security.apiKey.entity.BaseApiKeyEntity;

@Entity
@Table(name = "admin_keys")
@NoArgsConstructor
@Getter
public class AdminKey extends BaseApiKeyEntity {
    
    public AdminKey (String value, String description) {
        super(value, description);
    }
    
}
