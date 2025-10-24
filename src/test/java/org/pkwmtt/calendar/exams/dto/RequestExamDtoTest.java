package org.pkwmtt.calendar.exams.dto;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.calendar.adnotations.CorrectFutureDateValidator;
import org.pkwmtt.utils.UtilsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestExamDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = createValidator();
    }

    @Test
    void shouldSuccessWithCompleteData() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when, then
        assertTrue(validator.validate(requestExamDto).isEmpty());
    }

    @Test
    void shouldSuccessWithEmptyDescription() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when, then
        assertTrue(validator.validate(requestExamDto).isEmpty());
    }

    @Test
    void shouldSuccessWithBlankDescription() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when, then
        assertTrue(validator.validate(requestExamDto).isEmpty());
    }

    @Test
    void shouldSuccessWithBlankSubgroups() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .build();
        //when, then
        assertTrue(validator.validate(requestExamDto).isEmpty());
    }

    @Test
    void shouldSuccessWithEmptySubgroups() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
        //when, then
        assertTrue(validator.validate(requestExamDto).isEmpty());
    }


    //    empty Strings
    @Test
    void shouldFailWithEmptyTitle() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
        // when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailWithBlankTitle() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of(""))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void shouldFailWithEmptyGeneralGroups() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of())
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("generalGroups")));
    }

    @Test
    void shouldFailWithBlankGeneralGroups() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("generalGroups")));
    }

    @Test
    void shouldFailWithTooLongTitle() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                //256 characters
                .title("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .description("First exam")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void toLongDescription() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                //256 characters
                .description("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
                .date(LocalDateTime.now().plusDays(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    void dateIsNull() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("Math exam")
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("date")));
    }

    @Test
    void dateNotInFuture() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("Math exam")
                .date(LocalDateTime.now().minusHours(1))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("date")));
    }

    @Test
    void dateTooFarInFuture() {
        //given
        RequestExamDto requestExamDto = RequestExamDto.builder()
                .title("Math exam")
                .description("Math exam")
                .date(LocalDateTime.now().plusDays(365))
                .examType("exam")
                .generalGroups(Set.of("12K2"))
                .subgroups(Set.of("L04"))
                .build();
        //when
        Set<ConstraintViolation<RequestExamDto>> violations = validator.validate(requestExamDto);
        //then
        assertFalse(validator.validate(requestExamDto).isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("date")));
    }

    private Validator createValidator() {
        UtilsService utilsServiceMock = mock(UtilsService.class);
        lenient().when(utilsServiceMock.getEndOfSemester()).thenReturn(Optional.of(LocalDate.now().plusDays(10)));

        ConstraintValidatorFactory factory = new ConstraintValidatorFactory() {
            @Override
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
                if (key.equals(CorrectFutureDateValidator.class)) {
                    return (T) new CorrectFutureDateValidator(utilsServiceMock);
                }
                try {
                    return key.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            @Override
            public void releaseInstance(ConstraintValidator<?, ?> instance) {}
        };

        return Validation.byDefaultProvider()
                .configure()
                .constraintValidatorFactory(factory)
                .buildValidatorFactory()
                .getValidator();
    }

}