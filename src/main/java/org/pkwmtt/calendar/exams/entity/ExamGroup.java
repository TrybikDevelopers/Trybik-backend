package org.pkwmtt.calendar.exams.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exams_groups")
public class ExamGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_group_id")
    private Integer examGroupId;

    @Column(name = "exam_id", nullable = false)
    private Integer examId;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;
}

