package org.pkwmtt.calendar.exams.repository;

import org.pkwmtt.calendar.exams.entity.ExamGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamGroupRepository extends JpaRepository<ExamGroup, Integer> {
}

