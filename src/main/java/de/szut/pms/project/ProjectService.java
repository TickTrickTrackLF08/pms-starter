package de.szut.pms.project;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final ProjectMapper mapper;

    public ProjectService(ProjectRepository repository, ProjectMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ProjectDTO create(CreateProjectDTO dto) {
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
