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

}
