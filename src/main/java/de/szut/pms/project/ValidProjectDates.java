package de.szut.pms.project;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = ProjectDatesValidator.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface ValidProjectDates {

    String message() default "The start date must be before the end date.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}