package de.szut.pms.projectemployee;

public class EmployeeAlreadyAssignedException extends RuntimeException {

    public EmployeeAlreadyAssignedException(Long employeeId, Long projectId) {
        super("Mitarbeiter mit der ID " + employeeId +
                " ist bereits dem Projekt mit der ID " + projectId + " zugeordnet.");
    }
}