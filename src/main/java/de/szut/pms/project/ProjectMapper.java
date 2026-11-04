package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.projectemployee.ProjectEmployeeDTO;
import de.szut.pms.projectemployee.ProjectEmployeeMapper;
import org.springframework.stereotype.Component;
import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProjectMapper {

    private final ProjectEmployeeMapper projectEmployeeMapper;

    public ProjectMapper(ProjectEmployeeMapper projectEmployeeMapper) {
        this.projectEmployeeMapper = projectEmployeeMapper;
    }

    public ProjectDTO toDTO(Project entity) {
        ProjectDTO dto = new ProjectDTO();

        dto.setId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setComment(entity.getComment());
        dto.setCustomerServiceRepresentative(entity.getCustomerServiceRepresentative());
        dto.setCustomerId(entity.getCustomerId());
        dto.setEndDate(entity.getEndDate());
        dto.setStartDate(entity.getStartDate());
        dto.setResponsibleEmployeeId(entity.getResponsibleEmployeeId());
        dto.setProjectEmployees(toEmployeeDTOs(entity.getEmployees()));
        return dto;
    }

    public ProjectEmployeesDTO toProjectEmployeesDTO(Project entity) {
        ProjectEmployeesDTO dto = new ProjectEmployeesDTO();
        dto.setProjectId(entity.getId());
        dto.setDescription(entity.getDescription());
        dto.setEmployees(toEmployeeDTOs(entity.getEmployees()));
        return dto;
    }

    public Project toEntity(CreateProjectDTO dto) {
        Project entity = new Project();

        entity.setDescription(dto.description());
        entity.setResponsibleEmployeeId(dto.responsibleEmployeeId());
        entity.setCustomerId(dto.customerId());
        entity.setCustomerServiceRepresentative(
                dto.customerServiceRepresentative()
        );
        entity.setComment(dto.comment());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());

        if (dto.projectEmployees() != null) {
            for (CreateProjectEmployeeDTO employeeDTO : dto.projectEmployees()) {
                ProjectEmployee projectEmployee = new ProjectEmployee();
                projectEmployee.setEmployeeId(employeeDTO.employeeId());
                projectEmployee.setQualificationID(
                        employeeDTO.qualificationID()
                );

                entity.addEmployee(projectEmployee);
            }
        }

        return entity;
    }

    public void updateEntity(Project entity, UpdateProjectDTO dto) {

        entity.setDescription(dto.description());
        entity.setResponsibleEmployeeId(dto.responsibleEmployeeId());
        entity.setCustomerId(dto.customerId());

        entity.setCustomerServiceRepresentative(
                dto.customerServiceRepresentative()
        );

        entity.setComment(dto.comment());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
    }

    private List<ProjectEmployeeDTO> toEmployeeDTOs(List<ProjectEmployee> employees) {
        List<ProjectEmployeeDTO> dtos = new ArrayList<>();
        for (ProjectEmployee employee : employees) {
            dtos.add(projectEmployeeMapper.toDTO(employee));
        }
        return dtos;
    }
}
