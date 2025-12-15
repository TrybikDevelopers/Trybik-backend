package org.pkwmtt.calendar.adnotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CorrectFutureDateValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CorrectFutureDate {

    String message() default "Wrong date!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
