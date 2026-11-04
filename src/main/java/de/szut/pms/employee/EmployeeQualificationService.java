package de.szut.pms.employee;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.project.Project;
import de.szut.pms.projectemployee.ProjectEmployee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeQualificationService {
    private final EmployeeClient employeeClient;

    public EmployeeQualificationService(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }


    /**
     * Regarding the task: “Ensure that the project is not created if an error occurs”
     * This is ensured when the method is called, as it throws an exception and terminates
     * if the qualification is not present
     *
     * For assigning a single employee to a project.
     * @param projectEmployee the employee to be assigned to a new project.
     */
    public void validateQualification(ProjectEmployee projectEmployee){
        validateQualification(projectEmployee.getEmployeeId(), projectEmployee.getQualificationID());
    }

    /**
     * For assigning a single employee (based on the ID and qualification, not the Employee entity)
     *
     * @param employeeId    the employee to be matched
     * @param qualificationID the desired qualification ID for the project
     */
    public void validateQualification(Long employeeId, Long qualificationID) {
        EmployeeDto employee = employeeClient.findById(employeeId);

        for (SkillDto skill : employee.skillSet()) {
            if (skill.id().equals(qualificationID)) {
                return;
            }
        }
        throw new QualificationMissingException(employeeId, qualificationID);
    }

    /**
     * For creating a project.
     * @param employees the list of employees for the project to be created
     */
    public void validateEmployees(List<ProjectEmployee> employees) {
        for (ProjectEmployee employee : employees) {
            validateQualification(employee.getEmployeeId(), employee.getQualificationID());
        }
    }

    /**
     * Checks whether an employee exists without verifying any qualifications.
     */
    public void validateEmployee(Long employeeId) {
        EmployeeDto employee = employeeClient.findById(employeeId);

        if (employee == null) {
            throw new ResourceNotFoundException(
                    "Employee with ID " + employeeId + " not found"
            );
        }
    }

    /**
     * For creating a project
     * @param project the project to be created
     */
    public void validateEmployees(Project project) {
        validateEmployees(project.getEmployee());
    }
}
