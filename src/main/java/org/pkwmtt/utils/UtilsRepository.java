package org.pkwmtt.utils;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilsRepository extends JpaRepository<UtilsProperty, Integer> {
    Optional<UtilsProperty> findByKey(String key);
}

