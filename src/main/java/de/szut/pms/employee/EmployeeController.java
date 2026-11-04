package de.szut.pms.employee;

import de.szut.pms.project.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ProblemDetail;
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

    @Operation(summary = "Get a list of all projects an employee is working on")
    @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
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
            description = "Missing or invalid token",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
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
            description = "Employee not found",
            content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                        {
                          "detail": "Employee with ID 10 not found.",
                          "instance": "/employees/10/projects",
                          "status": 404,
                          "title": "Not found"
                        }
                        """)
            )
    )
    @GetMapping("/{employeeId}/projects")
    public ResponseEntity<EmployeeProjectsDTO> getEmployeeProjects(@PathVariable Long employeeId) {
        return ResponseEntity.ok(projectService.findByEmployeeId(employeeId));
    }
}