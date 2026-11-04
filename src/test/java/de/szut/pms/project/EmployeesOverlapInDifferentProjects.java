package de.szut.pms.project;

import de.szut.pms.employee.EmployeeDto;
import de.szut.pms.employee.SkillDto;
import de.szut.pms.project.Project;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.testcontainers.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;


import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class EmployeesOverlapInDifferentProjects extends AbstractIntegrationTest {
    private final String validRequest = """
        {
          "description": "Testprojekt für Zeitüberschneidung",
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
                                List.of(new SkillDto("Qualifikation 3",3L))));
    }

    @Test
    @WithMockUser
    void createProjectWithoutTimeOverlap() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 1, 1));
        existingProject.setEndDate(LocalDate.of(2026, 1, 10));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());

        Assertions.assertEquals(countBefore + 2, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithTimeOverlapOnLastDayReturns409() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 2, 28));
        existingProject.setEndDate(LocalDate.of(2026, 11, 30));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isConflict());

        Assertions.assertEquals(countBefore + 1, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithTimeOverlapFirstDayReturns409() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2025, 2, 28));
        existingProject.setEndDate(LocalDate.of(2026, 2, 1));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isConflict());

        Assertions.assertEquals(countBefore + 1, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithNoTimeOverlapEdgeCaseAtBeginning() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 1, 28));
        existingProject.setEndDate(LocalDate.of(2026, 1, 31));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());

        Assertions.assertEquals(countBefore + 2, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithNoTimeOverlapEdgeCaseAtEnd() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 3, 1));
        existingProject.setEndDate(LocalDate.of(2026, 12, 1));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isCreated());

        Assertions.assertEquals(countBefore + 2, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithTimeOverlapOverHoleProjectReturns409() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 1, 1));
        existingProject.setEndDate(LocalDate.of(2026, 12, 1));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isConflict());

        Assertions.assertEquals(countBefore + 1, projectRepository.count());
    }

    @Test
    @WithMockUser
    void createProjectWithTimeOverlapAtTimeWithinReturns409() throws Exception {

        long countBefore = projectRepository.count();

        Project existingProject = new Project();
        existingProject.setDescription("Bestehendes Projekt");
        existingProject.setResponsibleEmployeeId(1L);
        existingProject.setCustomerId(1L);
        existingProject.setCustomerServiceRepresentative("Max");
        existingProject.setStartDate(LocalDate.of(2026, 2, 10));
        existingProject.setEndDate(LocalDate.of(2026, 2, 20));

        ProjectEmployee employee = new ProjectEmployee();
        employee.setProject(existingProject);
        employee.setEmployeeId(12L);
        employee.setQualificationID(3L);

        existingProject.getEmployees().add(employee);

        projectRepository.save(existingProject);

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isConflict());

        Assertions.assertEquals(countBefore + 1, projectRepository.count());
    }


}
