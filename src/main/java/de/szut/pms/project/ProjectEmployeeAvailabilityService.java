package de.szut.pms.project;

import de.szut.pms.projectemployee.ProjectEmployee;
import org.springframework.stereotype.Service;

import java.util.List;

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

        for(ProjectEmployee employee: givenProject.getEmployee()){
            for(Project project: projects){
                if(!isSameProject(project, givenProject)){
                    for(ProjectEmployee projectEmployee: project.getEmployee()){
                        if(isSameEmployee(projectEmployee, employee)){
                            if(timeOverlaps(project, givenProject)){
                                throw new EmployeeTimeConflictException(givenProject.getId(), project.getId(), employee.getEmployeeId());
                            }
                        }
                    }
                }
            }
        }
    }



    private boolean timeOverlaps(Project project, Project givenProject) {
        return  !givenProject.getEndDate().isBefore(project.getStartDate()) &&
                !project.getEndDate().isBefore(givenProject.getStartDate());
    }
    private boolean isSameProject(Project project, Project givenProject) {
        return project.getId().equals(givenProject.getId());
    }
    private boolean isSameEmployee(ProjectEmployee projectEmployee, ProjectEmployee employee) {
        return projectEmployee.getEmployeeId().equals(employee.getEmployeeId());
    }

}
