package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployeeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProjectEmployeesDTO {  //A separate DTO because it precisely defines what the client sees and needs, and ensures there are no empty fields in the JSON
    private Long projectId;
    private String description;
    private List<ProjectEmployeeDTO> employees;

}
