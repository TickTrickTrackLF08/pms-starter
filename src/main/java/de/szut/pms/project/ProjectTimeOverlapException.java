package de.szut.pms.project;

public class ProjectTimeOverlapException extends RuntimeException {

    public ProjectTimeOverlapException(Long employeeId) {
        super("Der Mitarbeiter mit der ID " + employeeId
                + " ist im angegebenen Zeitraum bereits einem anderen Projekt zugeordnet.");
    }
}