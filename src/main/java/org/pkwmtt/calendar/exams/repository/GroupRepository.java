package org.pkwmtt.calendar.exams.repository;

import org.pkwmtt.calendar.exams.entity.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GroupRepository extends JpaRepository<StudentGroup, Integer> {
    Set<StudentGroup> findAllByNameIn(Set<String> names);
}