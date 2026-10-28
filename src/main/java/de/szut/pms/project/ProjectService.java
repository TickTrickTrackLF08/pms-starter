package de.szut.pms.project;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void deleteById(Long id){
        Project project = projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException(id));
        projectRepository.delete(project);
    }



}
