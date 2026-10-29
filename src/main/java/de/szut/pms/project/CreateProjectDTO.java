package de.szut.pms.project;

import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateProjectDTO(
        @NotNull(message = "Die Projektbeschreibung darf nicht fehlen.")
        @Size(min = 10, message = "Die Beschreibung muss mindestens 10 Zeichen lang sein.")
        String description,

        @NotNull(message = "Ein verantwortlicher interner Mitarbeiter muss angegeben werden.")
        Long responsibleEmployeeId,

        @NotNull(message = "Ein Kunde muss angegeben werden.")
        Long customerId,

        @NotNull(message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        @Size(min = 1, message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        String customerServiceRepresentative,

        String comment,

        @NotNull(message = "Das Startdatum muss angegeben werden.")
        LocalDate startDate,

        @NotNull(message = "Das Enddatum muss angegeben werden.")
        LocalDate endDate,

        @Valid
        List<CreateProjectEmployeeDTO> projectEmployees
        ) {
}