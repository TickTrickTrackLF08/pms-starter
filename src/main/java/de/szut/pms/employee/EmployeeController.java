package de.szut.pms.employee;

import de.szut.pms.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final ProjectService projectService;

    public EmployeeController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Operation(summary = "Erhalte eine Liste aller Projekte, an denen ein Mitarbeiter arbeitet")
    @ApiResponse(
            responseCode = "200",
            description = "Projekte erfolgreich abgerufen",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = EmployeeProjectsDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "employeeId": 1,
                          "projects": [
                            {
                              "id": 24,
                              "description": "Project",
                              "startDate": "2026-11-01",
                              "endDate": "2027-02-28",
                              "qualificationId": 1
                            }
                          ]
                        }
                        """)
            )
    )
    @ApiResponse(
            responseCode = "401",
            description = "Kein oder ungültiger Token",
            content = @Content(
                    mediaType = "application/problem+json",
                    examples = @ExampleObject(value = """
                        {
                          "detail": "The request was unauthorized, please check your credentials",
                          "status": 401,
                          "title": "No token is provided"
                        }
                        """)
            )
    )
    @ApiResponse(
            responseCode = "404",
            description = "Mitarbeiter nicht gefunden",
            content = @Content(
                    mediaType = "application/problem+json",
                    examples = @ExampleObject(value = """
                        {
                          "detail": "Mitarbeiter mit ID 10 nicht gefunden",
                          "instance": "/employees/10/projects",
                          "status": 404,
                          "title": "Nicht gefunden"
                        }
                        """)
            )
    )
    @GetMapping("/{employeeId}/projects")
    public ResponseEntity<EmployeeProjectsDTO> getEmployeeProjects(@PathVariable Long employeeId) {
        return ResponseEntity.ok(projectService.findByEmployeeId(employeeId));
    }
}