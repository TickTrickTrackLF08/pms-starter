package de.szut.pms.project;

public class EmployeeTimeConflictException extends RuntimeException {
    public EmployeeTimeConflictException(String message) {
        super(message);
    }
    public EmployeeTimeConflictException(Long givenProjectId, Long projectId, Long employeeId) {
        super("In deinem Projekt mit der ID " + givenProjectId + " überschneidet sich mindestens ein Mitarbeiter mit dem Project " + projectId + "." +
            " Zuletzt geprüfter Mitarbeiter: " + employeeId + ".");
    }

}
