package de.szut.pms.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddProjectEmployeeDTO(

        @NotNull(message = "An employee ID must be provided.")
        @Schema(example = "15")
        Long employeeId,

        @NotNull(message = "A qualification ID must be provided.")
        @Schema(example = "2")
        Long qualificationID

) {}