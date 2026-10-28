package de.szut.pms.project;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String message) {
        super(message);
    }
    public ProjectNotFoundException(Long id) {
        super("Das Projekt mit der Id: " + id + " konnte nicht gefunden werden/existiert nicht.");
    }
}
