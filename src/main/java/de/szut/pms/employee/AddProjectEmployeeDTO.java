package de.szut.pms.employee;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record AddProjectEmployeeDTO(

        @NotNull(message = "Eine Mitarbeiter-ID muss angegeben werden.")
        @Schema(example = "15")
        Long employeeId,

        @NotNull(message = "Eine Qualifikations-ID muss angegeben werden.")
        @Schema(example = "2")
        Long qualificationID

) {}