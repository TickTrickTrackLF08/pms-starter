package de.szut.pms.projectemployee;

import jakarta.validation.constraints.NotNull;

public record CreateProjectEmployeeDTO(
        @NotNull Long employeeId,
        @NotNull Long qualificationID
) {
}
