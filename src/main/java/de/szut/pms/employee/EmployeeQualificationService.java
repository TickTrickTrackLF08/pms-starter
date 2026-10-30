package de.szut.pms.employee;

import de.szut.pms.project.Project;
import de.szut.pms.projectemployee.ProjectEmployee;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeQualificationService {
    private final EmployeeClient employeeClient;

    public EmployeeQualificationService(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }


    /**
     * Zu der Task: 'Sicherstellen, dass bei einem Fehler das Projekt nicht entsteht'
     * Wird beim Aufruf der Methode sichergestellt, da diese eine Exception wirft und abbricht,
     * wenn die Qualifikation nicht vorhanden ist
     *
     *
     * Für das Zuweisen eines einzelnen Mitarbeiters zu einem Projekt.
     * @param projectEmployee der Mitarbieter, der einem neuem Projekt zugeordnet werden soll.
     */
    public void validateQualification(ProjectEmployee projectEmployee){
        validateQualification(projectEmployee.getEmployeeId(), projectEmployee.getQualificationID());
    }
    /**
     * Für das Zuweisen eines einzelnen Mitarbeiter (anhand der Id und Qualifikation und nicht der Employee-Entität)
     *
     * @param employeeId    des abzugleichenden Mitarbeiters
     * @param qualificationID gewünschte QualifikationID für das Projekt
     */
    public void validateQualification(Long employeeId, Long qualificationID) {
        EmployeeDto employee = employeeClient.findById(employeeId);

        for (SkillDto skill : employee.skillSet()) {
            if (skill.id().equals(qualificationID)) {
                return;
            }
        }
        throw new QualificationMissingException(employeeId, qualificationID);
    }

    /**
     * Für das Anlegen eines Projektes.
     * @param employees die Mitarbeiterliste, des anzulegenden Projektes
     */
    public void validateEmployees(List<ProjectEmployee> employees) {
        for (ProjectEmployee employee : employees) {
            validateQualification(employee.getEmployeeId(), employee.getQualificationID());
        }
    }

    /**
     * Für das Anlegen eines Projektes
     * @param project welches angelegt werden soll
     */
    public void validateEmployees(Project project) {
        validateEmployees(project.getEmployee());
    }
}
