package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(Long id){super("Es gibt keinen Project Eintrag mit der Kennung " + id + ".");}
}
