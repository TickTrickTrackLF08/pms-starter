package de.szut.pms.project;

public class EmployeeTimeConflictException extends RuntimeException {
    public EmployeeTimeConflictException(String message) {
        super(message);
    }
    public EmployeeTimeConflictException(Long givenProjectId, Long projectId, Long employeeId) {
        super("In your project with the ID " + givenProjectId + " at least one employee overlaps with the project " + projectId + "." +
            "Most Recently Audited Employee: " + employeeId + ".");
    }

}
