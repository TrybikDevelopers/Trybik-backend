package org.pkwmtt.calendar.exams.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.UnsupportedCountOfArgumentsException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Builder(builderClassName = "Builder")
@RequiredArgsConstructor
@Table(name = "exams")
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Integer examId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "exam_date", nullable = false)
    private LocalDateTime examDate;

    @ManyToOne
    @JoinColumn(name = "exam_type_id", nullable = false)
    private ExamType examType;
    
    @ManyToMany
    @JoinTable(
            name="exams_groups",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<StudentGroup> groups = new HashSet<>();

    @SuppressWarnings("unused")
    public static class Builder {
        public Exam build() {
//          min 1 max 100 elements of set
            if(groups == null)
                throw new UnsupportedCountOfArgumentsException(1, 100, null);
            if(groups.isEmpty() || groups.size() > 100)
                throw new UnsupportedCountOfArgumentsException(1, 100, groups.size());
            return new Exam(examId, title, description, examDate, examType, groups);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Exam exam = (Exam) o;
        return getTitle().equals(exam.getTitle()) &&
                Objects.equals(getDescription(), exam.getDescription()) &&
                getExamDate().truncatedTo(ChronoUnit.MINUTES).equals(exam.getExamDate().truncatedTo(ChronoUnit.MINUTES)) &&
                getExamType().equals(exam.getExamType()) &&
                getGroups().equals(exam.getGroups());
    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + Objects.hashCode(getDescription());
        result = 31 * result + getExamDate().truncatedTo(ChronoUnit.MINUTES).hashCode();
        result = 31 * result + getExamType().hashCode();
        result = 31 * result + getGroups().hashCode();
        return result;
    }
}