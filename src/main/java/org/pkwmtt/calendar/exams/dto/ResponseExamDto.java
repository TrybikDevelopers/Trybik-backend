package org.pkwmtt.calendar.exams.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ResponseExamDto extends RequestExamDto {

    private int examId;

}
