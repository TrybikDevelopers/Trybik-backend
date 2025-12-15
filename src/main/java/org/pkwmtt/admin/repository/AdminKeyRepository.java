package org.pkwmtt.admin.repository;

import org.pkwmtt.admin.entity.AdminKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminKeyRepository extends JpaRepository<AdminKey, Integer> {
    boolean existsApiKeyByValue (String value);
}
