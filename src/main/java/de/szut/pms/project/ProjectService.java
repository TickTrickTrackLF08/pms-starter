package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.customer.CustomerNotFoundException;
import de.szut.pms.employee.AddProjectEmployeeDTO;
import de.szut.pms.employee.EmployeeProjectDTO;
import de.szut.pms.employee.EmployeeProjectsDTO;
import de.szut.pms.employee.EmployeeQualificationService;
import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import de.szut.pms.projectemployee.EmployeeAlreadyAssignedException;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.projectemployee.ProjectEmployeeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import de.szut.pms.projectemployee.ProjectEmployeeNotFoundException;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final CustomerClient customerClient;
    private final EmployeeQualificationService qualificationService;
    private final ProjectEmployeeAvailabilityService availabilityService;



    public ProjectService(ProjectRepository repository, ProjectMapper mapper, CustomerClient customerClient, EmployeeQualificationService qualificationService, ProjectEmployeeAvailabilityService availabilityService) {
        this.repository = repository;
        this.mapper = mapper;
        this.customerClient = customerClient;
        this.qualificationService = qualificationService;
        this.availabilityService = availabilityService;
    }


    @Transactional
    public ProjectDTO create(CreateProjectDTO dto) {

        // Check if the customer exists
        if (!customerClient.existsById(dto.customerId())) {
            throw new CustomerNotFoundException(dto.customerId());
        }

        // Check whether the employee exists and has the specified qualification. If employees have been added
        if (dto.projectEmployees() != null) {
            for (CreateProjectEmployeeDTO projectEmployee : dto.projectEmployees()) {
                qualificationService.validateQualification(
                        projectEmployee.employeeId(),
                        projectEmployee.qualificationID()
                );
            }
        }

        // Check whether the project manager exists
        qualificationService.validateEmployee(dto.responsibleEmployeeId());

        //Create project
        Project project = mapper.toEntity(dto);

        //Check if employees are available at that time
        availabilityService.validateNoTimeOverlap(project);

        // Save project
         Project saved = repository.save(project);

        return mapper.toDTO(saved);
    }


    //Transaction is necessary because the mapper reads the LAZY-loaded employee list; without an open session, a LazyInitializationException would occur.
    //findById + orElseThrow throws a ProjectNotFoundException (404) if the ID is unknown
    @Transactional
    public ProjectDTO findOneByID(Long id) {
        Project project =  repository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        return mapper.toDTO(project);
    }

    @Transactional
    public void deleteById(Long id){
        Project project = repository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException(id));
        repository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectOverviewDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toOverviewDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeProjectsDTO findByEmployeeId(Long employeeId) {
        qualificationService.validateEmployee(employeeId);

        List<EmployeeProjectDTO> projects = repository.findByEmployeeId(employeeId).stream()
                .map(project -> {
                    Long qualificationId = project.getEmployee().stream()
                            .filter(pe -> pe.getEmployeeId().equals(employeeId))
                            .map(ProjectEmployee::getQualificationID)
                            .findFirst()
                            .orElse(null);

                    return new EmployeeProjectDTO(
                            project.getId(),
                            project.getDescription(),
                            project.getStartDate(),
                            project.getEndDate(),
                            qualificationId
                    );
                })
                .toList();

        return new EmployeeProjectsDTO(employeeId, projects);
    }


    /**
     * Removes an employee from a project.
     *
     * @param projectId the ID of the project from which the employee should be removed
     * @param employeeId the ID of the employee to remove
     * @return a DTO containing the updated project information and employee list,
     * according to the response criteria
     * @throws ProjectNotFoundException if no project with the given ID exists
     * @throws ProjectEmployeeNotFoundException if the employee is not assigned to the project
     */
    @Transactional
    public ProjectEmployeeRemoveDTO removeEmployee(Long projectId, Long employeeId){
        Project project = repository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(projectId));

        ProjectEmployee deleteEmployee = null;
        for(ProjectEmployee employee: project.getEmployee()){
            if(employeeId.equals(employee.getEmployeeId())){
                deleteEmployee = employee;
                break;
            }
        }
        if(deleteEmployee != null){
            project.getEmployee().remove(deleteEmployee);
        }else{
            throw new ProjectEmployeeNotFoundException(projectId, employeeId);
        }
        repository.save(project);
        return mapper.toRemoveEmployeeDTO(project);
    }

    //Transaction is necessary because the mapper reads the LAZY-loaded employee list; without an open session, a LazyInitializationException would occur.
    //findById + orElseThrow throws a ProjectNotFoundException (404) if the ID is unknown
    @Transactional
    public ProjectEmployeesDTO findAllEmployeesOfProjectOneByID(Long id) {
        Project project =  repository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        return mapper.toProjectEmployeesDTO(project);
    }

    @Transactional
    public ProjectDTO addEmployeeToProject(Long projectId, AddProjectEmployeeDTO dto) {

        // Prüfen, ob das Projekt existiert
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // Prüfen, ob Mitarbeiter bereits zugeordnet ist
        boolean alreadyAssigned = project.getEmployee().stream()
                .anyMatch(employee ->
                        employee.getEmployeeId().equals(dto.employeeId()));
        // Falls der Mitarbeiter bereits zugeordnet ist, wird eine Exception geworfen
        if (alreadyAssigned) {
            throw new EmployeeAlreadyAssignedException(
                    dto.employeeId(),
                    projectId
            );
        }

        // Prüfen ob der Mitarbeiter existiert und die angegebene Qualifikation besitzt
        qualificationService.validateQualification(
                dto.employeeId(),
                dto.qualificationID()
        );

        ProjectEmployee projectEmployee = new ProjectEmployee();
        projectEmployee.setEmployeeId(dto.employeeId());
        projectEmployee.setQualificationID(dto.qualificationID());

        project.addEmployee(projectEmployee);

        // Prüfen ob der Mitarbeiter im Zeitraum des Projekts bereits in einem anderen Projekt arbeitet
        availabilityService.validateNoTimeOverlap(project);

        Project saved = repository.save(project);

        return mapper.toDTO(saved);
    }

    @Transactional
    public ProjectDTO update(Long id, UpdateProjectDTO dto) {

        Project project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        // Kunde prüfen
        if (!customerClient.existsById(dto.customerId())) {
            throw new CustomerNotFoundException(dto.customerId());
        }

        // Projektleiter prüfen
        qualificationService.validateEmployee(
                dto.responsibleEmployeeId()
        );

        // Prüfen, ob sich der Projektzeitraum verändert hat
        boolean datesChanged =
                !project.getStartDate().equals(dto.startDate())
                        || !project.getEndDate().equals(dto.endDate());

        if (datesChanged) {

            // Alle bereits zugeordneten Mitarbeiter prüfen
            for (ProjectEmployee employee : project.getEmployee()) {

                boolean overlapping = repository.existsOverlappingProject(
                        employee.getEmployeeId(),
                        id,
                        dto.startDate(),
                        dto.endDate()
                );

                if (overlapping) {
                    throw new ProjectTimeOverlapException(
                            employee.getEmployeeId()
                    );
                }
            }
        }

        // Projektdaten aktualisieren
        mapper.updateEntity(project, dto);

        Project updated = repository.save(project);

        return mapper.toDTO(updated);
    }

}
