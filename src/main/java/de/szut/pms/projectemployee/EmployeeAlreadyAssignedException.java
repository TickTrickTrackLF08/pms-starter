package de.szut.pms.projectemployee;

public class EmployeeAlreadyAssignedException extends RuntimeException {

    public EmployeeAlreadyAssignedException(Long employeeId, Long projectId) {
        super("Employee with ID " + employeeId +
                " is already associated with the project with the ID " + projectId + " assigned.");
    }
}