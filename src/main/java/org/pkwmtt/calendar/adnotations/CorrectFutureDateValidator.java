package org.pkwmtt.calendar.adnotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.utils.UtilsService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class CorrectFutureDateValidator implements ConstraintValidator<CorrectFutureDate, LocalDateTime> {

    private final UtilsService utilsService;

    @Override
    public boolean isValid(LocalDateTime time, ConstraintValidatorContext constraintValidatorContext) {
        if (isNull(time)) {
            setMessage(constraintValidatorContext, "must not be null");
            return false;
        }

        if (time.isBefore(LocalDateTime.now())){
            setMessage(constraintValidatorContext, "Date must be in the future");
            return false;
        }

        LocalDate endOfSemester;
        var utilOptional = utilsService.getEndOfSemester();
        if (utilOptional.isPresent()) {
            endOfSemester = utilOptional.get();
        } else {
            setMessage(constraintValidatorContext, "End of semester date is not configured");
            return false;        }

        if (time.isAfter(endOfSemester.atStartOfDay())) {
            setMessage(constraintValidatorContext, "Date is too far in the future");
            return false;
        }

        return true;
    }

    private void setMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
