package de.szut.pms.project;

import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectControllerTests extends AbstractIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    @AfterEach
    void cleanUp() {
        projectRepository.deleteAll();
    }

    private Project saveProject(String description) {
        Project project = new Project();
        project.setDescription(description);
        project.setResponsibleEmployeeId(1L);
        project.setCustomerId(1L);
        project.setCustomerServiceRepresentative("Maxi Heinz");
        project.setComment("Test project");
        project.setStartDate(LocalDate.of(2026, 1, 1));
        project.setEndDate(LocalDate.of(2026, 12, 31));
        return projectRepository.save(project);
    }


    //Check to see if no Id was passed
    @Test
    @WithMockUser
    void getByIdWithIdReturnsOk() throws Exception {
        Project saved = saveProject("new project");
        mockMvc.perform(get("/projects/" + saved.getId()))
                .andExpect(status().is(200));
    }


    // Non existin Project
    @Test
    @WithMockUser
    void getByIdWithUnknownIdReturnsNotFound() throws Exception {
        mockMvc.perform(get("/projects/999999"))
                .andExpect(status().isNotFound());
    }

}
