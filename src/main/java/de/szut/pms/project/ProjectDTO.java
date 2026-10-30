package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployeeDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class ProjectDTO {
     private Long id;
     private String description;
     private Long responsibleEmployeeId;
     private Long customerId;
     private String customerServiceRepresentative;
     private String comment;
     private LocalDate startDate;
     private LocalDate endDate;
     private List<ProjectEmployeeDTO> projectEmployees;
}
