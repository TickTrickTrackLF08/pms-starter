package de.szut.pms.projectemployee;

import de.szut.pms.common.ResourceNotFoundException;

public class ProjectEmployeeNotFoundException extends ResourceNotFoundException {
    public ProjectEmployeeNotFoundException(Long id){super("Es gibt keinen ProjectEmployee Eintrag mit der Kennung " + id + ".");}
    public ProjectEmployeeNotFoundException(Long projectId, Long employeeId){
        super("In dem Projekt mit der Id " + projectId + " existiert kein Mitarbeiter mit der Id " + employeeId + ".");
    }
}
