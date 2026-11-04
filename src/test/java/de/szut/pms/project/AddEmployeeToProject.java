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

public class AddEmployeeToProject extends AbstractIntegrationTest {

    private final String validRequest = """
            {
               "employeeId": 1,
               "qualificationID": 3
             }
        """;

    @BeforeEach
    void setup() {
        when(employeeClient.findById(1L)).thenReturn(new EmployeeDto(
                1L,
                "Mustermann",
                "Max",
                "",
                "",
                "",
                "",
                List.of(new SkillDto("Qualifikation 3",3L))));
    }


    @Test
    @WithMockUser
    void addEmployeeToProjectWithValidIdAndValidQualification()
            throws Exception {

        // Create a project
        Project project = new Project();
        project.setDescription("Test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        project = projectRepository.save(project);

        // Call the REST endpoint
        mockMvc.perform(
                post("/projects/" + project.getId() + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest)
        ).andExpect(status().isOk());

        // Check if the employee was added
        Assertions.assertEquals(1, projectEmployeeRepository.count());

        // Retrieve the saved assignment
        ProjectEmployee assignment = projectEmployeeRepository.findAll().get(0);

        // Verify that the saved data matches the request
        Assertions.assertEquals(1L, assignment.getEmployeeId());
        Assertions.assertEquals(3L, assignment.getQualificationID());
        Assertions.assertEquals(project.getId(), assignment.getProject().getId());
    }


    @Test
    @WithMockUser
    void addEmployeeToProjectWithInvalidQualification() throws Exception {

        // Create a project
        Project project = new Project();
        project.setDescription("Test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        project = projectRepository.save(project);

        // Mock employee with a different qualification
        when(employeeClient.findById(1L)).thenReturn(
                new EmployeeDto(
                        1L, "Mustermann", "Max", "", "", "", "",
                        List.of(new SkillDto("Qualification 5", 5L))
                )
        );

        // Call the REST endpoint
        mockMvc.perform(
                post("/projects/" + project.getId() + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest)
        ).andExpect(status().isConflict());

        // Check that no employee was assigned
        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }


    @Test
    @WithMockUser
    void addEmployeeToProjectWithNonExistingEmployee() throws Exception {
        // Create a project
        Project project = new Project();
        project.setDescription("Test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        project = projectRepository.save(project);

        // Mock a non-existing employee
        when(employeeClient.findById(10L)).thenThrow(
                new ResourceNotFoundException("Employee with ID 10 not found")
        );

        String request = """
        {
            "employeeId": 10,
            "qualificationID": 1
        }
        """;

        // Call the REST endpoint
        mockMvc.perform(
                post("/projects/" + project.getId() + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request)
        ).andExpect(status().isNotFound());

        // Check that no employee was assigned
        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }


    @Test
    @WithMockUser
    void addEmployeeToProjectWithoutQualification() throws Exception {

        // Create a project
        Project project = new Project();
        project.setDescription("Test project");
        project.setResponsibleEmployeeId(2L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Max");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 1, 10));

        project = projectRepository.save(project);

        // Call the REST endpoint without a qualification ID
        mockMvc.perform(
                post("/projects/" + project.getId() + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "employeeId": 1
                            }
                            """)
        ).andExpect(status().isBadRequest());

        // Check that no employee was assigned
        Assertions.assertEquals(0, projectEmployeeRepository.count());
    }

}
