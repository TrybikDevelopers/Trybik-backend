package org.pkwmtt.calendar.exams.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.pkwmtt.calendar.adnotations.CorrectFutureDate;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@SuperBuilder
public class RequestExamDto {

    @NotBlank
    @Size(max = 255, message = "max size of field is 255")
    private String title;

    @Size(max = 255, message = "max size of field is 255")
    private String description;

    @CorrectFutureDate
    private LocalDateTime date;

    @NotNull
    private String examType;

    @NotEmpty
    @Size(min = 1)
    private Set<String> generalGroups;

    private Set<String> subgroups;
}
