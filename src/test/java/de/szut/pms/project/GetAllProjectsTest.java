package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetAllProjectsTest extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void cleanUp() {
        projectRepository.deleteAll();
    }

    private Project saveProject(String description, Long... employeeIds) {
        Project project = new Project();
        project.setDescription(description);
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Maxi Heinz");
        project.setComment("Test project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        for (Long employeeId : employeeIds) {
            ProjectEmployee pe = new ProjectEmployee();
            pe.setProject(project);          // Set the owning side, otherwise project_id stays null
            pe.setEmployeeId(employeeId);
            pe.setQualificationID(1L);
            project.getEmployee().add(pe);
        }
        return projectRepository.save(project);
    }

    @Test
    @WithMockUser
    void getAllProjectsReturnsProjectWithEmployees() throws Exception {
        saveProject("New project", 1L);

        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description").value("New project"))
                .andExpect(jsonPath("$[0].projectEmployees.length()").value(1));
    }

    @Test
    @WithMockUser
    void getAllProjectsWithoutDataReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}