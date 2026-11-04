package de.szut.pms.employee;

public class QualificationMissingException extends RuntimeException {
    public QualificationMissingException(String message) {
        super(message);
    }
    public  QualificationMissingException(Long id, Long qualification) {
        super("The Employee " + id + " holds the qualification with the ID " + qualification +" not!");
    }
}


