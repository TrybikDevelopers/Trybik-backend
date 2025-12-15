package org.pkwmtt.calendar.adnotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class CorrectFutureDateValidator implements ConstraintValidator<CorrectFutureDate, LocalDateTime> {

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

        //TODO Date need to be extracted to f.e DB (this date is end of semester, maybe have to change to +1 month after end of semester)
        if (time.isAfter(LocalDateTime.of(2026, 2, 22, 0, 0))) {
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
