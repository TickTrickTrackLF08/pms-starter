package de.szut.pms.project;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(Long id){super("Es gibt keinen Eintrag mit der Kennung " + id + ".");}
}
