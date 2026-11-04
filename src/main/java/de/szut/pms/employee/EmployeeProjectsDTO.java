package de.szut.pms.employee;

import java.util.List;

public record EmployeeProjectsDTO(
        Long employeeId,
        List<EmployeeProjectDTO> projects
) {}