package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import de.szut.pms.customer.CustomerClient;
import de.szut.pms.customer.CustomerNotFoundException;
import de.szut.pms.employee.EmployeeQualificationService;
import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import de.szut.pms.projectemployee.ProjectEmployee;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final CustomerClient customerClient;
    private final EmployeeQualificationService qualificationService;

    public ProjectService(ProjectRepository repository, ProjectMapper mapper, CustomerClient customerClient, EmployeeQualificationService qualificationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.customerClient = customerClient;
        this.qualificationService = qualificationService;
    }

    @Transactional
    public ProjectDTO create(CreateProjectDTO dto) {

        // Prüfen, ob der Kunde existiert
        if (!customerClient.existsById(dto.customerId())) {
            throw new CustomerNotFoundException(dto.customerId());
        }

        // Prüfen, ob der Mitarbeiter existiert und die angegebene qualifikation besitzt. Falls Mitarbeiter gesetzt wurden
        if (dto.projectEmployees() != null) {
            for (CreateProjectEmployeeDTO projectEmployee : dto.projectEmployees()) {
                qualificationService.validateQualification(
                        projectEmployee.employeeId(),
                        projectEmployee.qualificationID()
                );
            }
        }

        // Prüfen, ob der Projektleiter existiert
        qualificationService.validateEmployee(dto.responsibleEmployeeId());

        // Projekt erstellen und speichern
        Project saved = repository.save(mapper.toEntity(dto));

        return mapper.toDTO(saved);
    }


    @Transactional(readOnly = true)
    public List<ProjectDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

}
