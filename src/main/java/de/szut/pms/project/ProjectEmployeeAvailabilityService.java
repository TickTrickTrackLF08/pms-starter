package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ProjectEmployeeAvailabilityService {

    private final ProjectRepository repository;

    public ProjectEmployeeAvailabilityService(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a project, that is about to be saved and checks whether it's employees bolong already
     * to another project
     * @param givenProject that should be saved/created/changed
     */
    public void validateNoTimeOverlap(Project givenProject) {

        List<Project> projects = repository.findAll();

        for (ProjectEmployee employee : givenProject.getEmployees()) {

            for (Project project : projects) {

                // Aktuelles Projekt ausschließen
                if (isSameProject(project, givenProject)) {
                    continue;
                }

                // Projekte ohne zeitliche Überschneidung überspringen
                if (!timeOverlaps(project, givenProject)) {
                    continue;
                }

                // Prüfen, ob der Mitarbeiter bereits im anderen Projekt arbeitet
                boolean employeeAlreadyAssigned = project.getEmployees()
                        .stream()
                        .anyMatch(projectEmployee ->
                                isSameEmployee(projectEmployee, employee));

                if (employeeAlreadyAssigned) {
                    throw new EmployeeTimeConflictException(
                            givenProject.getId(),
                            project.getId(),
                            employee.getEmployeeId()
                    );
                }
            }
        }
    }

    private boolean timeOverlaps(Project project, Project givenProject) {
        return !givenProject.getEndDate().isBefore(project.getStartDate())
                && !project.getEndDate().isBefore(givenProject.getStartDate());
    }

    private boolean isSameProject(Project project, Project givenProject) {
        return givenProject.getId() != null
                && Objects.equals(project.getId(), givenProject.getId());
    }

    private boolean isSameEmployee(
            ProjectEmployee projectEmployee,
            ProjectEmployee employee) {

        return Objects.equals(
                projectEmployee.getEmployeeId(),
                employee.getEmployeeId()
        );
    }
}