package de.szut.pms.project;

public class EmployeeTimeConflictException extends RuntimeException {

    public EmployeeTimeConflictException(String message) {
        super(message);
    }

    public EmployeeTimeConflictException(
            Long givenProjectId,
            Long projectId,
            Long employeeId) {

        super(
                (givenProjectId == null
                        ? "For the new project"
                        : "In your project with the ID " + givenProjectId)
                        + " At least one employee is involved in the project "
                        + projectId
                        + ". Employee ID: " + employeeId + "."
        );
    }
}