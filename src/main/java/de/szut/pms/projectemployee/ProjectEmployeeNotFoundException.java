package de.szut.pms.projectemployee;

import de.szut.pms.common.ResourceNotFoundException;

public class ProjectEmployeeNotFoundException extends ResourceNotFoundException {
    public ProjectEmployeeNotFoundException(Long id){super("There is no employee with the ID " + id + ".");}
    public ProjectEmployeeNotFoundException(Long projectId, Long employeeId){
        super("There is no employee with the ID " + employeeId + " in the project with the ID " + projectId + ".");
    }
}
