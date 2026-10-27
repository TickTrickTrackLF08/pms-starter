package de.szut.pms.projectemployee;

import org.springframework.stereotype.Component;

@Component
public class ProjectEmployeeMapper {

    public ProjectEmployeeDTO toDTO(ProjectEmployee entity) {
        ProjectEmployeeDTO dto = new ProjectEmployeeDTO();
        dto.setId(entity.getId());
        dto.setProjectId(entity.getProject().getId());
        dto.setEmployeeId(entity.getEmployeeId());
        dto.setQualificationId(entity.getQualificationID());
        return dto;
    }

    public ProjectEmployee toEntity(ProjectEmployeeDTO dto) {return new ProjectEmployee();}
}
