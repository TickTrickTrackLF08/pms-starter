package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.employee.EmployeeServiceNotAvailableException;
import de.szut.pms.employee.SkillDto;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddEmployeeToProjectTests extends AbstractIntegrationTest {

    @BeforeEach
    void setup() {
        when(employeeClient.findById(1L)).thenReturn(
                new EmployeeDto(
                        1L, "Mustermann", "Max", "", "", "", "",
                        List.of(new SkillDto("Qualifikation 3", 3L))
                )
        );
    }

    @Test
    @WithMockUser
    void addEmployeeToProjectWithValidIdAndValidQualification() throws Exception {
        Project project = createProject();

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                {
                    "employeeId": 1,
                    "qualificationID": 3
                }
                """))
                .andExpect(status().isOk());

        Assertions.assertEquals(1, projectEmployeeRepository.count());

        ProjectEmployee employee = projectEmployeeRepository.findAll().getFirst();

        Assertions.assertEquals(1L, employee.getEmployeeId());
        Assertions.assertEquals(3L, employee.getQualificationID());
        Assertions.assertEquals(project.getId(), employee.getProject().getId());
    }

    @Test
    @WithMockUser
    void addEmployeeToProjectWithInvalidQualification() throws Exception {
        Project project = createProject();

        when(employeeClient.findById(1L)).thenReturn(
                new EmployeeDto(
                        1L, "Mustermann", "Max", "", "", "", "",
                        List.of(new SkillDto("Qualification 5", 5L))
                )
        );

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                {
                    "employeeId": 1,
                    "qualificationID": 3
                }
                """))
                .andExpect(status().isConflict());

        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }

    @Test
    @WithMockUser
    void addEmployeeToProjectWithNonExistingEmployee() throws Exception {
        Project project = createProject();

        when(employeeClient.findById(10L)).thenThrow(
                new ResourceNotFoundException("Employee with ID 10 not found")
        );

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                {
                    "employeeId": 10,
                    "qualificationID": 1
                }
                """))
                .andExpect(status().isNotFound());

        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }

    @Test
    @WithMockUser
    void addEmployeeToProjectWithoutQualification() throws Exception {
        Project project = createProject();

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                {
                    "employeeId": 1
                }
                """))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }

    private Project createProject() {
        Project project = new Project();
        project.setDescription("Test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        return projectRepository.save(project);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder addEmployeeRequest(
            Long projectId, String json
    ) {
        return post("/projects/" + projectId + "/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
    }

    @Test
    @WithMockUser
    void addEmployeeToNonExistingProject() throws Exception {
        mockMvc.perform(addEmployeeRequest(999999L, """
                { "employeeId": 1, "qualificationID": 3 }
                """))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void addEmployeeTwiceReturnsConflict() throws Exception {
        Project project = createProject();
        String body = """
                { "employeeId": 1, "qualificationID": 3 }
                """;

        mockMvc.perform(addEmployeeRequest(project.getId(), body))
                .andExpect(status().isOk());
        mockMvc.perform(addEmployeeRequest(project.getId(), body))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, projectEmployeeRepository.count());
    }

    @Test
    @WithMockUser
    void addResponsibleEmployeeAsProjectEmployee() throws Exception {
        Project project = createProject(); // responsibleEmployeeId = 2

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                { "employeeId": 2, "qualificationID": 3 }
                """))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }

    @Test
    @WithMockUser
    void addEmployeeWithTimeOverlapReturnsConflict() throws Exception {
        Project project = createProject(); // 2026-01-01 bis 2026-01-10

        Project other = new Project();
        other.setDescription("Overlapping project");
        other.setResponsibleEmployeeId(3L);
        other.setCustomerId(1L);
        other.setCustomerServiceRepresentative("Max");
        other.setStartDate(LocalDate.of(2026, 1, 5));
        other.setEndDate(LocalDate.of(2026, 1, 15));
        ProjectEmployee assigned = new ProjectEmployee();
        assigned.setEmployeeId(1L);
        assigned.setQualificationID(3L);
        other.addEmployee(assigned);
        projectRepository.save(other);

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                { "employeeId": 1, "qualificationID": 3 }
                """))
                .andExpect(status().isConflict());

        Assertions.assertEquals(1, projectEmployeeRepository.count());
    }

    @Test
    @WithMockUser
    void addEmployeeWhenEmployeeServiceIsUnavailable() throws Exception {
        Project project = createProject();
        when(employeeClient.findById(1L)).thenThrow(
                new EmployeeServiceNotAvailableException("Employee Service is down"));

        mockMvc.perform(addEmployeeRequest(project.getId(), """
                { "employeeId": 1, "qualificationID": 3 }
                """))
                .andExpect(status().isServiceUnavailable());

        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }
}
