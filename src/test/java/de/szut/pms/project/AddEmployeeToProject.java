package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.employee.EmployeeDto;
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

class AddEmployeeToProject extends AbstractIntegrationTest {

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
}
