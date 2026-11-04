
package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.customer.CustomerNotFoundException;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.employee.EmployeeServiceNotAvailableException;
import de.szut.pms.employee.SkillDto;
import de.szut.pms.testcontainers.AbstractIntegrationTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser
class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @MockitoBean
    private EmployeeClient employeeClient;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    @BeforeEach
    void cleanUpProjects() {
        projectRepository.deleteAll();

        when(employeeClient.findById(12L)).thenReturn(
                new EmployeeDto(
                        12L, "Mustermann", "Max",
                        "Musterstraße 1", "12345", "Musterstadt",
                        "0123456789",
                        List.of(new SkillDto("Qualifikation 3", 3L))
                )
        );

        when(employeeClient.findById(15L)).thenReturn(
                new EmployeeDto(
                        15L, "Musterfrau", "Anna",
                        "Musterstraße 2", "12345", "Musterstadt",
                        "0123456789",
                        List.of(new SkillDto("Qualifikation 2", 2L))
                )
        );
    }

    private final String validRequest = """
            {
                "description": "Website-Relaunch für den Kunden",
                "responsibleEmployeeId": 12,
                "customerId": 5,
                "customerServiceRepresentative": "Anna Müller",
                "comment": "Neuer Webauftritt für 2027",
                "startDate": "2026-11-10",
                "endDate": "2027-02-28",
                "projectEmployees": [
                    {
                        "employeeId": 12,
                        "qualificationID": 3
                    },
                    {
                        "employeeId": 15,
                        "qualificationID": 2
                    }
                ]
            }
            """;

    @Test
    void createProjectSuccessfully() throws Exception {
        long countBefore = projectRepository.count();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description")
                        .value("Website-Relaunch für den Kunden"))
                .andExpect(jsonPath("$.responsibleEmployeeId").value(12))
                .andExpect(jsonPath("$.customerId").value(5))
                .andExpect(jsonPath("$.customerServiceRepresentative")
                        .value("Anna Müller"))
                .andExpect(jsonPath("$.projectEmployees.length()").value(2));

        assertEquals(countBefore + 1, projectRepository.count());
    }

    @Test
    void createProjectWithMissingEmployeeQualification() throws Exception {
        long countBefore = projectRepository.count();

        when(employeeClient.findById(12L)).thenReturn(
                new EmployeeDto(
                        12L, "Mustermann", "Max",
                        "Musterstraße 1", "12345", "Musterstadt",
                        "0123456789",
                        List.of(new SkillDto("Andere Qualifikation", 1L))
                )
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isConflict());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWhenEmployeeServiceIsUnavailableForProjectEmployee()
            throws Exception {
        long countBefore = projectRepository.count();

        when(employeeClient.findById(15L)).thenThrow(
                new EmployeeServiceNotAvailableException(
                        "Employee-Service ist nicht erreichbar"
                )
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isServiceUnavailable());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithNonExistingResponsibleEmployee() throws Exception {
        long countBefore = projectRepository.count();

        when(employeeClient.findById(99L)).thenReturn(null);

        String request = validRequest.replace(
                "\"responsibleEmployeeId\": 12",
                "\"responsibleEmployeeId\": 99"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWhenEmployeeServiceIsUnavailableForResponsibleEmployee()
            throws Exception {
        long countBefore = projectRepository.count();

        when(employeeClient.findById(12L)).thenThrow(
                new EmployeeServiceNotAvailableException(
                        "Employee-Service ist nicht erreichbar"
                )
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isServiceUnavailable());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutDescription() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"description\": \"Website-Relaunch für den Kunden\",",
                "\"description\": null,"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithTooShortDescription() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "Website-Relaunch für den Kunden",
                "Test"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutResponsibleEmployee() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"responsibleEmployeeId\": 12",
                "\"responsibleEmployeeId\": null"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutCustomerId() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"customerId\": 5",
                "\"customerId\": null"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutCustomerServiceRepresentative() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"customerServiceRepresentative\": \"Anna Müller\"",
                "\"customerServiceRepresentative\": null"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutStartDate() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"startDate\": \"2026-11-10\"",
                "\"startDate\": null"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithoutEndDate() throws Exception {
        long countBefore = projectRepository.count();

        String request = validRequest.replace(
                "\"endDate\": \"2027-02-28\"",
                "\"endDate\": null"
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, projectRepository.count());
    }

    @Test
    void createProjectWithStartDateAfterEndDate() throws Exception {
        String request = validRequest.replace(
                "\"startDate\": \"2026-11-10\"",
                "\"startDate\": \"2027-03-01\""
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProjectWithEqualStartAndEndDate() throws Exception {
        String request = validRequest.replace(
                "\"endDate\": \"2027-02-28\"",
                "\"endDate\": \"2026-11-10\""
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createProjectWithoutOptionalFields() throws Exception {
        long countBefore = projectRepository.count();

        String request = """
                {
                    "description": "Website-Relaunch für den Kunden",
                    "responsibleEmployeeId": 12,
                    "customerId": 5,
                    "customerServiceRepresentative": "Anna Müller",
                    "startDate": "2026-11-10",
                    "endDate": "2027-02-28"
                }
                """;

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value(nullValue()))
                .andExpect(jsonPath("$.projectEmployees").isEmpty());

        assertEquals(countBefore + 1, projectRepository.count());
    }

    @Test
    void findAllProjects() throws Exception {
        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].description",
                        hasItem("Website-Relaunch für den Kunden")));
    }

    @Test
    void createProjectWithNonExistingCustomer() {
        long countBefore = projectRepository.count();

        CreateProjectDTO dto = new CreateProjectDTO(
                "Website-Relaunch für den Kunden",
                12L,
                null,
                "Anna Müller",
                "Neuer Webauftritt für 2027",
                java.time.LocalDate.of(2026, 11, 10),
                java.time.LocalDate.of(2027, 2, 28),
                null
        );

        assertThrows(
                CustomerNotFoundException.class,
                () -> projectService.create(dto)
        );

        assertEquals(countBefore, projectRepository.count());
    }
}
