package de.szut.pms.employee;

public class QualificationMissingException extends RuntimeException {
    public QualificationMissingException(String message) {
        super(message);
    }
    public  QualificationMissingException(Long id, String qualification) {
        super("Der Mitarbeiter" + id + " besitzt die Qualifikation " + qualification +" nicht!");
    }
}


