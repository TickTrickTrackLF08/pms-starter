package de.szut.pms.project;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProjectDatesValidator
        implements ConstraintValidator<ValidProjectDates, ProjectDateRange> {

    @Override
    public boolean isValid(ProjectDateRange value, ConstraintValidatorContext context) {
        if (value == null || value.startDate() == null || value.endDate() == null) {
            return true;
        }

        return value.startDate().isBefore(value.endDate());
    }
}