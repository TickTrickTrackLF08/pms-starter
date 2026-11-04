package de.szut.pms.employee;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.project.Project;
import de.szut.pms.project.ProjectRepository;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EmployeeControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void cleanUp() {
        projectRepository.deleteAll();
    }

    private Project saveProjectWithEmployee(String description, Long employeeId) {
        Project project = new Project();
        project.setDescription(description);
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Maxi Heinz");
        project.setComment("Test project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));

        ProjectEmployee pe = new ProjectEmployee();
        pe.setProject(project);              // Set the owning side, otherwise project_id stays null
        pe.setEmployeeId(employeeId);
        pe.setQualificationID(1L);
        project.getEmployee().add(pe);

        return projectRepository.save(project);
    }

    private EmployeeDto employee(Long id) {
        return new EmployeeDto(
                id,
                "Mustermann",
                "Max",
                "Musterstraße 1",
                "28195",
                "Bremen",
                "0421123456",
                List.of()   // no skills needed for this endpoint
        );
    }

    @Test
    @WithMockUser
    void getProjectsOfEmployeeReturnsOk() throws Exception {
        when(employeeClient.findById(1L)).thenReturn(employee(1L));
        saveProjectWithEmployee("New project", 1L);

        mockMvc.perform(get("/employees/1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(1))
                .andExpect(jsonPath("$.projects.length()").value(1))
                .andExpect(jsonPath("$.projects[0].description").value("New project"))
                .andExpect(jsonPath("$.projects[0].qualificationId").value(1));
    }

    @Test
    @WithMockUser
    void getProjectsOfUnknownEmployeeReturnsNotFound() throws Exception {
        when(employeeClient.findById(99L))
                .thenThrow(new ResourceNotFoundException("Employee with ID 99 not found."));

        mockMvc.perform(get("/employees/99/projects"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getProjectsWhenEmployeeServiceIsDownReturnsServiceUnavailable() throws Exception {
        when(employeeClient.findById(1L))
                .thenThrow(new EmployeeServiceNotAvailableException("Employee service is not available."));

        mockMvc.perform(get("/employees/1/projects"))
                .andExpect(status().isServiceUnavailable());
    }
}