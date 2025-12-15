package org.pkwmtt.calendar.exams.repository;

import org.pkwmtt.calendar.exams.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExamTypeRepository extends JpaRepository<ExamType, Integer> {
    Optional<ExamType> findByName(String name);
}