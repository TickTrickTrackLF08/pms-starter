package de.szut.pms.projectemployee;

import de.szut.pms.common.ResourceNotFoundException;

public class ProjectEmployeeNotFoundException extends ResourceNotFoundException {
    public ProjectEmployeeNotFoundException(Long id){super("Es gibt keinen ProjectEmployee Eintrag mit der Kennung " + id + ".");}
}
