package de.szut.pms.project;

public class ProjectTimeOverlapException extends RuntimeException {

    public ProjectTimeOverlapException(Long employeeId) {
        super("The employee with ID " + employeeId
                + " is already assigned to another project during the specified time period.");
    }
}