package de.szut.pms.projectemployee;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectEmployeeDTO {
    private Long id;
    private Long projectId;
    private Long employeeId;
    private int qualificationId;
}
