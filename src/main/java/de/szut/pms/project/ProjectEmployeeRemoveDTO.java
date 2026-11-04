package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployeeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectEmployeeRemoveDTO {
    private Long id;
    private String description;
    private List<ProjectEmployeeDTO> projectEmployees;
}
