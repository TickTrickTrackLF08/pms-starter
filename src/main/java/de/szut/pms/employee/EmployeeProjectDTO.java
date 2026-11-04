package de.szut.pms.employee;

import java.time.LocalDate;

public record EmployeeProjectDTO(
        Long id,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Long qualificationId
) {}