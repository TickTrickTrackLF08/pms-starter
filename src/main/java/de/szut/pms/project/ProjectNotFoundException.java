package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(Long id){super("There is no project entry with the ID " + id + ".");}
}
