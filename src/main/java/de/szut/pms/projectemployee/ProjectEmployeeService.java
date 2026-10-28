package de.szut.pms.projectemployee;

import org.springframework.stereotype.Service;

@Service
public class ProjectEmployeeService {

    private final ProjectEmployeeRepository repository;
    private final ProjectEmployeeMapper mapper;

    public ProjectEmployeeService(ProjectEmployeeRepository repository, ProjectEmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
}
