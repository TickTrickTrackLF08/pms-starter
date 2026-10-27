package de.szut.pms.project;

import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectDTO toDTO(Project entity) {
        ProjectDTO dto = new ProjectDTO();
        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setComment(entity.getComment());
        dto.setCustomerServiceRepresentative(entity.getCustomerServiceRepresentative());
        dto.setCustomerId(entity.getCustomerId());
        dto.setEndDate(entity.getEndDate());
        dto.setStartDate(entity.getStartDate());
        return dto;
    }

    public Project toEntity(ProjectDTO dto) {return new Project();}
}
