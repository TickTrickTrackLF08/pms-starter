package de.szut.pms.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;

    public ProjectService(ProjectRepository repository, ProjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }
    @Transactional
    public void deleteById(Long id){
        Project project = repository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException(id));
        repository.delete(project);
    }

}
