package org.pkwmtt.studentCodes.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.calendar.enities.SuperiorGroup;
import org.pkwmtt.calendar.exams.entity.StudentCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StudentCodeRepository extends JpaRepository<StudentCode, Integer> {
    Optional<StudentCode> findByCode(String code);

    boolean existsBySuperiorGroup(SuperiorGroup superiorGroup);

    boolean existsByCode(String code);

    @Query("UPDATE StudentCode sc SET sc.usage = sc.usage + 1 WHERE sc.code = ?1")
    @Modifying
    @Transactional
    void increaseUsageByCode(String code);
    
    @Transactional
    void deleteBySuperiorGroup(SuperiorGroup superiorGroup);
    
}