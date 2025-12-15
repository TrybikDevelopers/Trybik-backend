package org.pkwmtt.calendar.exams.mapper;

import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.calendar.exams.dto.RequestExamDto;
import org.pkwmtt.calendar.exams.dto.ResponseExamDto;
import org.pkwmtt.calendar.exams.entity.Exam;
import org.pkwmtt.calendar.exams.entity.ExamType;
import org.pkwmtt.calendar.exams.entity.StudentGroup;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for mapping Exam entity to RequestExamDto and RequestExamDto to Exam entity
 */
@Slf4j
public class ExamDtoMapper {
    private ExamDtoMapper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * @param requestExamDto requestExamDto object received from request
     * @return Exam entity WITHOUT examId which should be assigned by database
     *         Also contains examType field converted from String do ExamType
     */
    public static Exam mapToNewExam(RequestExamDto requestExamDto, Set<StudentGroup> groups, ExamType examType) {
        return Exam.builder()
                .title(requestExamDto.getTitle())
                .description(requestExamDto.getDescription())
                .examDate(requestExamDto.getDate())
                .examType(examType)
                .groups(groups)
                .build();
    }

    /**
     * @param requestExamDto requestExamDto object received from request
     * @param id of Exam that need to be modified
     * @return Exam entity WITH examId that allow to update entity in database instead of creating new one
     *         Also contains examType field converted from String do ExamType
     */
    public static Exam mapToExistingExam(RequestExamDto requestExamDto, Set<StudentGroup> groups, ExamType examType, int id) {
        return Exam.builder()
                .examId(id)
                .title(requestExamDto.getTitle())
                .description(requestExamDto.getDescription())
                .examDate(requestExamDto.getDate())
                .examType(examType)
                .groups(groups)
                .build();
    }

    /**
     * @param exams Set of Exams that would be mapped
     * @return List of ExamDtos
     */
    public static List<ResponseExamDto> mapToExamDto(Set<Exam> exams) {
        return exams.stream().map(ExamDtoMapper::mapToExamDto).collect(Collectors.toList());
    }

    /**
     * @param exam single exam that would be mapped
     * @return RequestExamDto
     */
    public static ResponseExamDto mapToExamDto(Exam exam) {
        Set<String> groups = exam.getGroups().stream().map(StudentGroup::getName).collect(Collectors.toSet());
        Set<String> generalGroups = groups.stream().filter(group -> Character.isDigit(group.charAt(0))).collect(Collectors.toSet());
        Set<String> subgroups = groups.stream().filter(group -> Character.isAlphabetic(group.charAt(0))).collect(Collectors.toSet());
        if(groups.size() != subgroups.size() + generalGroups.size())
            log.warn("Some groups of {} were not consumed in ExamDtoMapper.mapToExamDto()", groups);
        return ResponseExamDto.builder()
                .examId(exam.getExamId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .date(exam.getExamDate())
                .examType(exam.getExamType().getName())
                .generalGroups(generalGroups)
                .subgroups(subgroups)
                .build();
    }
}
