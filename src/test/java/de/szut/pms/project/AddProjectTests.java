package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.employee.SkillDto;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddProjectTests extends AbstractIntegrationTest {

    @Test
    @WithMockUser
    void createProjectSuccessfully() throws Exception {
        mockDependencies();

        mockMvc.perform(createRequest(projectJson(2L, 1L, null)))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithEmployeesSuccessfully() throws Exception {
        mockDependencies();

        mockMvc.perform(createRequest(projectJson(2L, 1L, 1L)))
                .andExpect(status().isCreated());

        Assertions.assertEquals(1, projectRepository.count());
        Assertions.assertEquals(1, projectEmployeeRepository.count());

        ProjectEmployee employee = projectEmployeeRepository.findAll().getFirst();

        Assertions.assertEquals(1L, employee.getEmployeeId());
        Assertions.assertEquals(3L, employee.getQualificationID());
    }

    @Test
    @WithMockUser
    void createProjectWithMissingDescription() throws Exception {
        mockDependencies();

        mockMvc.perform(createRequest("""
                {
                    "responsibleEmployeeId": 2,
                    "customerId": 1,
                    "customerServiceRepresentative": "Max",
                    "startDate": "2026-03-01",
                    "endDate": "2026-03-10"
                }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createProjectWithInvalidDateRange() throws Exception {
        mockDependencies();

        mockMvc.perform(createRequest("""
                {
                    "description": "Test project description",
                    "responsibleEmployeeId": 2,
                    "customerId": 1,
                    "customerServiceRepresentative": "Max",
                    "startDate": "2026-03-10",
                    "endDate": "2026-03-01"
                }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void createProjectWithNonExistingCustomer() throws Exception {
        when(customerClient.existsById(99L)).thenReturn(false);

        mockMvc.perform(createRequest(projectJson(2L, 99L, null)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createProjectWithNonExistingResponsibleEmployee() throws Exception {
        when(employeeClient.findById(99L)).thenThrow(
                new ResourceNotFoundException("Employee with ID 99 not found"));

        mockMvc.perform(createRequest(projectJson(99L, 1L, null)))
                .andExpect(status().isNotFound());

        Assertions.assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithNonExistingProjectEmployee() throws Exception {
        mockValidEmployee(2L);
        when(employeeClient.findById(99L)).thenThrow(
                new ResourceNotFoundException("Employee with ID 99 not found"));

        mockMvc.perform(createRequest(projectJson(2L, 1L, 99L)))
                .andExpect(status().isNotFound());

        Assertions.assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithOverlappingEmployeePeriod() throws Exception {
        createProjectWithAssignedEmployee();
        mockDependencies();

        mockMvc.perform(createRequest(projectJson(2L, 1L, 1L)))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, projectRepository.count());
    }

    private void mockDependencies() {
        when(customerClient.existsById(1L)).thenReturn(true);
        mockValidEmployee(1L);
        mockValidEmployee(2L);
    }

    private void mockValidEmployee(Long employeeId) {
        when(employeeClient.findById(employeeId)).thenReturn(
                new EmployeeDto(
                        employeeId,
                        "Mustermann",
                        "Max",
                        "",
                        "",
                        "",
                        "",
                        List.of(new SkillDto("Qualifikation 3", 3L))
                )
        );
    }

    private Project createProjectWithAssignedEmployee() {
        Project project = new Project();
        project.setDescription("Existing test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 3, 1));
        project.setEndDate(LocalDate.of(2026, 3, 10));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setEmployeeId(1L);
        employee.setQualificationID(3L);
        project.addEmployee(employee);

        return projectRepository.save(project);
    }

    private String projectJson(Long responsibleEmployeeId, Long customerId, Long employeeId) {
        String employees = "";

        if (employeeId != null) {
            employees = """
                    ,"projectEmployees": [
                        {
                            "employeeId": %d,
                            "qualificationID": 3
                        }
                    ]
                    """.formatted(employeeId);
        }

        return """
                {
                    "description": "Test project description",
                    "responsibleEmployeeId": %d,
                    "customerId": %d,
                    "customerServiceRepresentative": "Max",
                    "comment": "Test comment",
                    "startDate": "2026-03-01",
                    "endDate": "2026-03-10"
                    %s
                }
                """.formatted(responsibleEmployeeId, customerId, employees);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder createRequest(
            String json
    ) {
        return post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }

    @Test
    @WithMockUser
    void createProjectWithMissingQualification() throws Exception {
        mockDependencies();
        when(employeeClient.findById(1L)).thenReturn(new EmployeeDto(
                1L, "Mustermann", "Max", "", "", "", "",
                List.of(new SkillDto("Qualification 5", 5L))));

        mockMvc.perform(createRequest(projectJson(2L, 1L, 1L)))
                .andExpect(status().isConflict());

        Assertions.assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithResponsibleEmployeeAsProjectEmployee() throws Exception {
        mockDependencies();

        mockMvc.perform(createRequest(projectJson(2L, 1L, 2L)))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(0, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWhenEmployeeServiceIsUnavailable() throws Exception {
        when(employeeClient.findById(1L)).thenThrow(
                new EmployeeServiceNotAvailableException("Employee Service is down"));

        mockMvc.perform(createRequest(projectJson(2L, 1L, 1L)))
                .andExpect(status().isServiceUnavailable());

        Assertions.assertEquals(0, projectRepository.count());
    }

}
