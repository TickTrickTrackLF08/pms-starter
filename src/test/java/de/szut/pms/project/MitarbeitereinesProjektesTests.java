package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MitarbeitereinesProjektesTests extends AbstractIntegrationTest {

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
            pe.setProject(project);          // Owning Side setzen, sonst bleibt project_id null
            pe.setEmployeeId(employeeId);
            pe.setQualificationID(1L);
            project.getEmployee().add(pe);
        }
        return projectRepository.save(project);
    }

    @Test
    @WithMockUser
    void getEmployeesOfProjectByIdWithIdReturnsOk() throws Exception {
        Project saved = saveProject("new project", 1L, 2L);
        mockMvc.perform(get("/projects/" + saved.getId() + "/employees"))
                .andExpect(status().is(200));
    }

    @Test
    @WithMockUser
    void getEmployeesOfProjectWithoutEmployeesReturnsEmptyList() throws Exception {
        Project saved = saveProject("Empty project");

        mockMvc.perform(get("/projects/" + saved.getId() + "/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employees").isEmpty());
    }


    @Test
    @WithMockUser
    void getEmployeesOfProjectWithUnknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/projects/999999/employees"))
                .andExpect(status().isNotFound());
    }

}
