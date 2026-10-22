package de.szut.pms.employee;

public class EmployeeServiceNotAvailableException extends RuntimeException {
    public EmployeeServiceNotAvailableException(String message) {
        super(message);
    }
}
