package org.pkwmtt.calendar.exams.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.calendar.exams.entity.Representative;
import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;
import java.util.UUID;

public interface RepresentativeRepository extends JpaRepository<Representative, UUID> {
    Optional<Representative> findByEmail (String email);
    
    Optional<Representative> findBySuperiorGroup (SuperiorGroup superiorGroup);
    
    @Modifying
    @Transactional
    void deleteRepresentativeByEmail (String email);
}

