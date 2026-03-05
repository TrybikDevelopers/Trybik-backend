package org.pkwmtt.calendar.exams.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.pkwmtt.exceptions.UnsupportedCountOfArgumentsException;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * tests of custom Exam builder
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExamTest {

    ExamType examType;
    Set<StudentGroup> studentGroups;
    LocalDateTime date;

    @BeforeAll
    void setup(){
        examType = ExamType.builder().name("project").build();
        studentGroups = Set.of(StudentGroup.builder().name("12K2").build());
        date = LocalDateTime.now().plusDays(1);

    }

    @Test
    void shouldBuildExamWithCorrectData() {
        Exam exam = Exam.builder()
                .title("title")
                .description("description")
                .examDate(date)
                .examType(examType)
                .groups(studentGroups)
                .build();

        assertEquals("title", exam.getTitle());
        assertEquals("description", exam.getDescription());
        assertEquals(date, exam.getExamDate());
        assertEquals(examType, exam.getExamType());
        assertEquals(studentGroups, exam.getGroups());
    }

    @Test
    void shouldThrowWhenNoGroupsAssigned() {
        assertThrows(UnsupportedCountOfArgumentsException.class, () -> Exam.builder()
                .title("title")
                .description("description")
                .examDate(date)
                .examType(examType)
//                no exam groups specified
                .build());
    }

    @Test
    void shouldThrowWhenZeroGroupsAssigned() {
        assertThrows(UnsupportedCountOfArgumentsException.class, () -> Exam.builder()
                .title("title")
                .description("description")
                .examDate(date)
                .examType(examType)
                .groups(Set.of())
                .build());
    }

    @Test
    void shouldThrowWhenToManyGroupsAssigned() {
        Set<StudentGroup> longStudentGroups = IntStream.range(0, 101)
                .mapToObj(i -> StudentGroup.builder().name("group" + i).build())
                .collect(Collectors.toSet());

        assertThrows(UnsupportedCountOfArgumentsException.class, () -> Exam.builder()
                .title("title")
                .description("description")
                .examDate(date)
                .examType(examType)
                .groups(longStudentGroups)
                .build());
    }
}