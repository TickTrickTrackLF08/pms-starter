package de.szut.pms.project;

import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ChangeProjectDetails extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    void updateProjectWithValidData() throws Exception {
        Project project = createProject();
        mockDependencies();

        mockMvc.perform(updateRequest(project.getId(), projectJson(
                        "Updated project description", "2026-02-01", "2026-02-10"
                )))
                .andExpect(status().isOk());

        Project updated = projectRepository.findById(project.getId()).orElseThrow();

        Assertions.assertEquals("Updated project description", updated.getDescription());
        Assertions.assertEquals("Updated comment", updated.getComment());
        Assertions.assertEquals(LocalDate.of(2026, 2, 1), updated.getStartDate());
    }

    @Test
    @WithMockUser
    void updateNonExistingProject() throws Exception {
        mockDependencies();

        mockMvc.perform(updateRequest(999L, projectJson(
                        "Valid project description", "2026-02-01", "2026-02-10"
                )))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateProjectWithMissingDescription() throws Exception {
        Project project = createProject();

        mockMvc.perform(updateRequest(project.getId(), """
                {
                    "comment": "Updated comment",
                    "customerId": 1,
                    "customerServiceRepresentative": "Max",
                    "startDate": "2026-02-01",
                    "endDate": "2026-02-10",
                    "responsibleEmployeeId": 2
                }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateProjectWithNewTimePeriod() throws Exception {
        Project project = createProject();
        mockDependencies();

        mockMvc.perform(updateRequest(project.getId(), projectJson(
                        "Updated project description", "2026-03-01", "2026-03-10"
                )))
                .andExpect(status().isOk());

        Project updated = projectRepository.findById(project.getId()).orElseThrow();

        Assertions.assertEquals(LocalDate.of(2026, 3, 1), updated.getStartDate());
        Assertions.assertEquals(LocalDate.of(2026, 3, 10), updated.getEndDate());
    }

    @Test
    @WithMockUser
    void updateProjectWithOverlappingEmployeePeriod() throws Exception {
        Project project1 = createProject();
        Project project2 = createProject();

        project1.setStartDate(LocalDate.of(2026, 1, 1));
        project1.setEndDate(LocalDate.of(2026, 1, 10));
        project2.setStartDate(LocalDate.of(2026, 3, 1));
        project2.setEndDate(LocalDate.of(2026, 3, 10));

        addEmployee(project1);
        addEmployee(project2);

        projectRepository.save(project1);
        projectRepository.save(project2);

        mockDependencies();

        mockMvc.perform(updateRequest(project1.getId(), projectJson(
                        "First test project", "2026-03-05", "2026-03-15"
                )))
                .andExpect(status().isConflict());
    }

    private Project createProject() {
        Project project = new Project();
        project.setDescription("First test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        return projectRepository.save(project);
    }

    private void addEmployee(Project project) {
        ProjectEmployee employee = new ProjectEmployee();
        employee.setEmployeeId(1L);
        employee.setQualificationID(3L);
        project.addEmployee(employee);
    }

    private void mockDependencies() {
        when(customerClient.existsById(1L)).thenReturn(true);
        when(employeeClient.findById(2L)).thenReturn(
                new EmployeeDto(2L, "Mustermann", "Max", "", "", "", "", List.of())
        );
    }

    private String projectJson(String description, String startDate, String endDate) {
        return """
                {
                    "description": "%s",
                    "comment": "Updated comment",
                    "customerId": 1,
                    "customerServiceRepresentative": "Max",
                    "startDate": "%s",
                    "endDate": "%s",
                    "responsibleEmployeeId": 2
                }
                """.formatted(description, startDate, endDate);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder updateRequest(
            Long projectId, String json
    ) {
        return put("/projects/" + projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }
}
