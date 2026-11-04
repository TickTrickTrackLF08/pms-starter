package de.szut.pms.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Data for Updating an Existing Project")
@ValidProjectDates
public record UpdateProjectDTO(

        @NotNull(message = "The project description is a must.")
        @Size(min = 10, message = "The description must be at least 10 characters long.")
        String description,

        String comment,

        @NotNull(message = "A customer must be specified.")
        Long customerId,

        @NotNull(message = "A contact person at the customer's company must be specified.")
        @Size(min = 1, message = "A contact person at the customer's company must be specified.")
        String customerServiceRepresentative,

        @NotNull(message = "The start date must be specified.")
        LocalDate startDate,

        @NotNull(message = "The end date must be specified.")
        LocalDate endDate,

        @NotNull(message = "A responsible internal employee must be designated.")
        Long responsibleEmployeeId

) implements ProjectDateRange {
}