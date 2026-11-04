package de.szut.pms.project;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.customer.CustomerNotFoundException;
import de.szut.pms.employee.EmployeeProjectDTO;
import de.szut.pms.employee.EmployeeProjectsDTO;
import de.szut.pms.employee.EmployeeQualificationService;
import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import de.szut.pms.projectemployee.EmployeeAlreadyAssignedException;
import de.szut.pms.projectemployee.ProjectEmployee;
import de.szut.pms.projectemployee.ProjectEmployeeNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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

        // Check if the responsibleEmployeeId is part of the projectEmployees
        if (dto.projectEmployees() != null &&
                dto.projectEmployees().stream()
                        .anyMatch(employee ->
                                employee.employeeId().equals(dto.responsibleEmployeeId()))) {

            throw new ResponsibleEmployeeAlreadyAssignedException(
                    dto.responsibleEmployeeId()
            );
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
    public List<ProjectDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeProjectsDTO findByEmployeeId(Long employeeId) {
        qualificationService.validateEmployee(employeeId);

        List<EmployeeProjectDTO> projects = repository.findByEmployeeId(employeeId).stream()
                .map(project -> {
                    Long qualificationId = project.getEmployees().stream()
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
    public ProjectEmployeesDTO removeEmployee(Long projectId, Long employeeId){
        Project project = repository.findById(projectId)
                .orElseThrow(()->new ProjectNotFoundException(projectId));

        ProjectEmployee deleteEmployee = null;
        for(ProjectEmployee employee: project.getEmployees()){
            if(employeeId.equals(employee.getEmployeeId())){
                deleteEmployee = employee;
                break;
            }
        }
        if(deleteEmployee != null){
            project.getEmployees().remove(deleteEmployee);
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
    public ProjectDTO addEmployeeToProject(Long projectId, CreateProjectEmployeeDTO dto) {

        // Check whether the project exists
        Project project = repository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        // Check whether an employee has already been assigned
        boolean alreadyAssigned = project.getEmployees().stream()
                .anyMatch(employee ->
                        employee.getEmployeeId().equals(dto.employeeId()));
        // If the employee is already assigned, an exception is thrown
        if (alreadyAssigned) {
            throw new EmployeeAlreadyAssignedException(
                    dto.employeeId(),
                    projectId
            );
        }

        // Check whether the employee exists and has the specified qualification
        qualificationService.validateQualification(
                dto.employeeId(),
                dto.qualificationID()
        );

        ProjectEmployee projectEmployee = new ProjectEmployee();
        projectEmployee.setEmployeeId(dto.employeeId());
        projectEmployee.setQualificationID(dto.qualificationID());

        project.addEmployee(projectEmployee);

        // Check whether the employee is already working on another project during the project period
        availabilityService.validateNoTimeOverlap(project);

        Project saved = repository.save(project);

        return mapper.toDTO(saved);
    }

    @Transactional
    public ProjectDTO update(Long id, UpdateProjectDTO dto) {

        Project project = repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        // Verify customer
        if (!customerClient.existsById(dto.customerId())) {
            throw new CustomerNotFoundException(dto.customerId());
        }

        // Project managers should review
        qualificationService.validateEmployee(
                dto.responsibleEmployeeId()
        );

        // Update Project Data
        mapper.updateEntity(project, dto);

        // Check availability using the updated data
        availabilityService.validateNoTimeOverlap(project);

        Project updated = repository.save(project);

        return mapper.toDTO(updated);
    }

}
