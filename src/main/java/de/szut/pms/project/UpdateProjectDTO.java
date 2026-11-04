package de.szut.pms.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Daten zum Aktualisieren eines bestehenden Projekts")
@ValidProjectDates
public record UpdateProjectDTO(

        @NotNull(message = "Die Projektbeschreibung darf nicht fehlen.")
        @Size(min = 10, message = "Die Beschreibung muss mindestens 10 Zeichen lang sein.")
        String description,

        String comment,

        @NotNull(message = "Ein Kunde muss angegeben werden.")
        Long customerId,

        @NotNull(message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        @Size(min = 1, message = "Ein Ansprechpartner beim Kunden muss angegeben werden.")
        String customerServiceRepresentative,

        @NotNull(message = "Das Startdatum muss angegeben werden.")
        LocalDate startDate,

        @NotNull(message = "Das Enddatum muss angegeben werden.")
        LocalDate endDate,

        @NotNull(message = "Ein verantwortlicher interner Mitarbeiter muss angegeben werden.")
        Long responsibleEmployeeId

) implements ProjectDateRange {
}