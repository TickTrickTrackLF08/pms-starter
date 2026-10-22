package de.szut.pms.employee;

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
     * @param employeeId    des abzugleichenden Mitarbeiters
     * @param qualification gewünschte Qualifikation für das Projekt
     */
    public void validateQualification(Long employeeId, String qualification) {
        EmployeeDto employee = employeeClient.findById(employeeId);

        for (SkillDto skills : employee.skillSet()) {
            if (skills.skill().equalsIgnoreCase(qualification)) {
                return;
            }
        }
        throw new QualificationMissingException(employeeId, qualification);
    }
}
    //Erstmal als Vorlage, weiß ja nicht, wie die Liste mit dem Mitarbeitern übergeben wird
    // --- TO BE CONTINUED ---

//    public void checkAllEmployeesAreQualified(List<ProjectEmployeeDto> employees){
//        for(ProjectEmployeeDto employee: employees){
//            validateQualification(employee.id(), employee.qualification());
//        }
//    }

