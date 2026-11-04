package de.szut.pms.project;

import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Data for Creating a New Project")
public record CreateProjectDTO(

        @Schema(description = "Project Description", example = "Website Relaunch for the Client")
        @NotNull(message = "The project description is a must")
        @Size(min = 10, message = "The description must be at least 10 characters long")
        String description,

        @Schema(description = "ID of the responsible internal employee", example = "1")
        @NotNull(message = "A responsible internal employee must be designated")
        Long responsibleEmployeeId,

        @Schema(description = "Customer ID", example = "5")
        @NotNull(message = "A customer must be specified")
        Long customerId,

        @Schema(description = "Customer Contact Person", example = "Anna Müller")
        @NotNull(message = "A contact person at the customer's company must be specified")
        @Size(min = 1, message = "A contact person at the customer's company must be specified")
        String customerServiceRepresentative,

        @Schema(description = "Additional comments on the project", example = "new Website for 2027")
        String comment,

        @Schema(description = "Project Start Date", example = "2026-11-01")
        @NotNull(message = "The start date must be specified")
        LocalDate startDate,

        @Schema(description = "Project End Date", example = "2027-02-28")
        @NotNull(message = "The end date must be specified")
        LocalDate endDate,

        @Schema(description = "Employees assigned to the project", example = """
                [
                  {
                    "employeeId": 1,
                    "qualificationID": 1
                  },
                  {
                    "employeeId": 2,
                    "qualificationID": 2
                  }
                ]
                """)
        List<@Valid CreateProjectEmployeeDTO> projectEmployees

) {

        public CreateProjectDTO {
                if (startDate != null && endDate != null
                        && !startDate.isBefore(endDate)) {
                        throw new IllegalArgumentException(
                                "The start date must be before the end date."
                        );
                }
        }

}