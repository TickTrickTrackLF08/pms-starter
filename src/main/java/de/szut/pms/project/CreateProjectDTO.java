package de.szut.pms.project;

import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Daten zum Anlegen eines neuen Projekts")
public record CreateProjectDTO(

        @Schema(description = "Beschreibung des Projekts", example = "Website-Relaunch für den Kunden")
        @NotNull(message = "Die Projektbeschreibung darf nicht fehlen.")
        @Size(min = 10, message = "Die Beschreibung muss mindestens 10 Zeichen lang sein.")
        String description,

        @Schema(description = "ID des verantwortlichen internen Mitarbeiters", example = "12")
        @NotNull(message = "Ein verantwortlicher interner Mitarbeiter muss angegeben werden.")
        Long responsibleEmployeeId,

        @Schema(description = "ID des Kunden", example = "5")
        @NotNull(message = "Ein Kunde muss angegeben werden.")
        Long customerId,

        @Schema(description = "Ansprechpartner beim Kunden", example = "Anna Müller")
        @NotNull(message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        @Size(min = 1, message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        String customerServiceRepresentative,

        @Schema(description = "Zusätzliche Kommentare zum Projekt", example = "Neuer Webauftritt für 2027")
        String comment,

        @Schema(description = "Startdatum des Projekts", example = "2026-11-01")
        @NotNull(message = "Das Startdatum muss angegeben werden.")
        LocalDate startDate,

        @Schema(description = "Enddatum des Projekts", example = "2027-02-28")
        @NotNull(message = "Das Enddatum muss angegeben werden.")
        LocalDate endDate,

        @Schema(description = "Mitarbeiter, die dem Projekt zugeordnet werden", example = """
                [
                  {
                    "employeeId": 12,
                    "qualificationID": 3
                  },
                  {
                    "employeeId": 15,
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
                                "Das Startdatum muss vor dem Enddatum liegen."
                        );
                }
        }

}