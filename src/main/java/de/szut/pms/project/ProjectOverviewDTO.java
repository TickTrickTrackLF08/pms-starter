package de.szut.pms.project;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@JsonPropertyOrder({
        "id",
        "description",
        "comment",
        "customerId",
        "customerServiceRepresentative",
        "startDate",
        "endDate",
        "responsibleEmployeeId",
})

public class ProjectOverviewDTO {
    private Long id;
    private String description;
    private Long responsibleEmployeeId;
    private Long customerId;
    private String customerServiceRepresentative;
    private String comment;
    private LocalDate startDate;
    private LocalDate endDate;
}
