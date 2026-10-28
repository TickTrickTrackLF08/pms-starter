package de.szut.pms.projectemployee;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projectemployees")
public class ProjectEmployeeController {

    private final ProjectEmployeeService service;

    public ProjectEmployeeController(ProjectEmployeeService service) {this.service = service;}
}
