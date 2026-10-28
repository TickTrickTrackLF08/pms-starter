package de.szut.pms.project;

import de.szut.pms.common.ResourceNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class ProjectNotFoundException extends ResourceNotFoundException {
    public ProjectNotFoundException(Long id){super("Es gibt keinen Projekteintrag mit der Kennung " + id + ".");}
}
