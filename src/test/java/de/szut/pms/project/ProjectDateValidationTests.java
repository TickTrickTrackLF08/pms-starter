
package de.szut.pms.project;

import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.employee.SkillDto;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectDateValidationTests extends AbstractIntegrationTest {

    private final String validRequest = """
        {
          "description": "Testprojekt für Datumsvalidierung",
          "responsibleEmployeeId": 12,
          "customerId": 5,
          "customerServiceRepresentative": "Anna Müller",
          "comment": "Test",
          "startDate": "2026-02-01",
          "endDate": "2026-02-28",
          "projectEmployees": [
            {
              "employeeId": 12,
              "qualificationID": 3
            }
          ]
        }
        """;

    @BeforeEach
    void setup() {
        when(employeeClient.findById(12L)).thenReturn(new EmployeeDto(
                12L,
                "Mustermann",
                "Max",
                "",
                "",
                "",
                "",
                List.of(new SkillDto("Qualifikation 3", 3L))));
    }

    @Test
    @WithMockUser
    void createProjectWithStartDateAfterEndDateReturns400() throws Exception {

        long countBefore = projectRepository.count();

        String invalidRequest = validRequest.replace(
                "\"startDate\": \"2026-02-01\"",
                "\"startDate\": \"2026-03-01\""
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(countBefore, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithEqualStartAndEndDateReturns400() throws Exception {

        long countBefore = projectRepository.count();

        String invalidRequest = validRequest.replace(
                "\"endDate\": \"2026-02-28\"",
                "\"endDate\": \"2026-02-01\""
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(countBefore, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithoutStartDateReturns400() throws Exception {

        long countBefore = projectRepository.count();

        String invalidRequest = validRequest.replace(
                "\"startDate\": \"2026-02-01\",",
                ""
        );

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(countBefore, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithValidDateRangeReturns201() throws Exception {

        long countBefore = projectRepository.count();

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());

        Assertions.assertEquals(countBefore + 1, projectRepository.count());
    }
}
