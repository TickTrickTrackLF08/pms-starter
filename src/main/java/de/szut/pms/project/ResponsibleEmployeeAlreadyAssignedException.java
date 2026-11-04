package de.szut.pms.project;

public class ResponsibleEmployeeAlreadyAssignedException extends RuntimeException {

    public ResponsibleEmployeeAlreadyAssignedException(Long employeeId) {
        super("Employee with ID " + employeeId +
                " cannot be both project manager and project employee.");
    }
}