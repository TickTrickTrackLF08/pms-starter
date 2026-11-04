package de.szut.pms.project;

import de.szut.pms.projectemployee.CreateProjectEmployeeDTO;
import de.szut.pms.projectemployee.ProjectEmployeeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }


    @Operation(summary = "Create a new project")
    @ApiResponse(responseCode = "201", description = "Project created", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "id": 1,
                          "description": "Website relaunch",
                          "responsibleEmployeeId": 1,
                          "customerId": 5,
                          "customerServiceRepresentative": "Anna Müller",
                          "comment": "New corporate website",
                          "startDate": "2026-11-01",
                          "endDate": "2027-02-28",
                          "projectEmployees": [
                            {
                              "id": 1,
                              "employeeId": 1,
                              "qualificationID": 1
                            },
                            {
                              "id": 2,
                              "employeeId": 2,
                              "qualificationID": 2
                            }
                          ]
                        }
                    """)
            )
    )
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                        {
                          "detail": "The request contains invalid fields.",
                          "instance": "/projects",
                          "status": 400,
                          "title": "Invalid input",
                          "errors": {
                            "startDate": "Start date must be provided."
                          }
                        }
                        """)
            )
    )


    @ApiResponse(responseCode = "503", description = "The Employee Service is unavailable")
    @ApiResponse(responseCode = "409", description="An employee does not have the required qualifications or at least one employee is already assigned to another project during the specified time period")
    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody @Valid CreateProjectDTO dto) {
        ProjectDTO created = service.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }


    @Operation(summary = "Get all employees of a project")
    @ApiResponse(responseCode = "200", description = "Project with its employees returned", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectEmployeesDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "projectId": 1,
                          "description": "Website relaunch",
                          "employees": [
                            {
                              "id": 1,
                              "employeeId": 1,
                              "qualificationID": 1
                            },
                            {
                              "id": 2,
                              "employeeId": 2,
                              "qualificationID": 2
                            }
                          ]
                        }
                        """)
            )
    )
    @ApiResponse(responseCode = "400", description = "Bad request, e.g. the ID is not a number", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class))
    )
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                        {
                          "detail": "There is no project entry with the ID 99.",
                          "instance": "/projects/99/employees",
                          "status": 404,
                          "title": "Not found"
                        }
                        """)
            )
    )
    @GetMapping(value = "{id}/employees", produces = "application/json")
    public ResponseEntity<ProjectEmployeesDTO> getEmployeesFromProject(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findAllEmployeesOfProjectOneByID(id));
    }


    @Operation(summary = "Get a project by ID")
    @ApiResponse(responseCode = "200", description = "Project returned", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "id": 1,
                          "description": "Website relaunch",
                          "responsibleEmployeeId": 1,
                          "customerId": 5,
                          "customerServiceRepresentative": "Anna Müller",
                          "comment": "New corporate website",
                          "startDate": "2026-11-01",
                          "endDate": "2027-02-28",
                          "projectEmployees": [
                            {
                              "id": 1,
                              "employeeId": 1,
                              "qualificationID": 1
                            }
                          ]
                        }
                        """)
            )
    )
    @ApiResponse(responseCode = "400", description = "Bad request, e.g. the ID is not a number", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class)
            )
    )
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                        {
                          "detail": "There is no project entry with the ID 99.",
                          "instance": "/projects/99",
                          "status": 404,
                          "title": "Not found"
                        }
                        """)
            )
    )
    @GetMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.findOneByID(id));
    }


    @Operation(summary = "Get all projects")
    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully", content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ProjectDTO.class)),
                    examples = @ExampleObject(value = """
                        [
                          {
                            "id": 1,
                            "description": "Website relaunch",
                            "responsibleEmployeeId": 1,
                            "customerId": 5,
                            "customerServiceRepresentative": "Anna Müller",
                            "comment": "New corporate website",
                            "startDate": "2026-11-01",
                            "endDate": "2027-02-28"
                          },
                          {
                            "id": 2,
                            "description": "CRM migration",
                            "responsibleEmployeeId": 3,
                            "customerId": 7,
                            "customerServiceRepresentative": "Jonas Becker",
                            "comment": "Move to the new CRM system",
                            "startDate": "2027-01-15",
                            "endDate": "2027-06-30",
                            "projectEmployees": [
                            {
                              "id": 1,
                              "employeeId": 1,
                              "qualificationID": 1
                            }
                          ]
                          }
                        ]
                        """)
            )
    )
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }


    @Operation(summary = "Delete a project")
    @ApiResponse(responseCode = "204", description = "Project successfully deleted (no response body)")
    @ApiResponse(responseCode = "404", description = "Project not found", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(value = """
                {
                  "detail": "There is no project entry with the ID 99.",
                  "instance": "/projects/99",
                  "status": 404,
                  "title": "Not found"
                }
                """)))
    @ApiResponse(responseCode = "401", description = "Invalid JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build(); // .ok() would return 200 instead of 204
    }


    @Operation(summary = "Remove an employee from a project")
    @ApiResponse(responseCode = "200", description = "Employee removed successfully", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectEmployeeDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "id": 1,
                          "description": "Website relaunch",
                          "projectEmployees": [
                            {
                              "id": 1,
                              "employeeId": 1,
                              "qualificationID": 1
                            }
                          ]
                        }
                        """)
            )
    )
    @ApiResponse(responseCode = "404", description = "The project does not exist or the employee is not assigned to it", content = @Content(
                    mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemDetail.class),
                    examples = @ExampleObject(value = """
                        {
                          "detail": "There is no employee with the ID 2 in the project with the ID 1.",
                          "instance": "/projects/1/employees/2",
                          "status": 404,
                          "title": "Not found"
                        }
                        """)
            )
    )
    @ApiResponse(responseCode = "401", description = "Invalid JWT")
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<ProjectEmployeesDTO> removeEmployee(@PathVariable Long projectId, @PathVariable Long employeeId) {
        ProjectEmployeesDTO projectEmployeeRemoveDTO = service.removeEmployee(projectId, employeeId);
        return ResponseEntity.ok(projectEmployeeRemoveDTO);
    }


    @Operation(summary = "Update the details of a project")
    @ApiResponse(responseCode = "200", description = "Project successfully updated", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class),  examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "description": "Website relaunch phase 2",
                  "responsibleEmployeeId": 1,
                  "customerId": 5,
                  "customerServiceRepresentative": "Maxi David",
                  "comment": "Extended scope",
                  "startDate": "2026-11-01",
                  "endDate": "2027-03-31",
                  "projectEmployees": [
                    { "id": 1, "employeeId": 2, "qualificationID": 2 }
                  ]
                }
                """)))
    @ApiResponse(responseCode = "400", description = "Invalid input end date before start date, or the new project manager is already a project employee", content = @Content(
            mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(value = """
                {
                  "detail": "The request contains invalid fields",
                  "instance": "/projects/1",
                  "status": 400,
                  "title": "Invalid input",
                  "errors": { "projectDates": "The start date must be before the end date." }
                }
                """)))
    @ApiResponse(responseCode = "404", description = "Project or referenced resource not found", content = @Content(
            mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(value = """
                {
                  "detail": "There is no project entry with the ID 99.",
                  "instance": "/projects/99",
                  "status": 404,
                  "title": "Not found"
                }
                """)))
    @ApiResponse(responseCode = "409", description = "During this period, the employee will be assigned to another project.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "503", description = "The Employee Service is unavailable.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProjectDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }


    @Operation(summary = "Add an employee to a project")
    @ApiResponse(responseCode = "200", description = "Employee successfully added", content = @Content(
            mediaType = "application/json", schema = @Schema(implementation = ProjectDTO.class), examples = @ExampleObject(value = """
                {
                  "id": 1,
                  "description": "Website relaunch",
                  "responsibleEmployeeId": 1,
                  "customerId": 5,
                  "customerServiceRepresentative": "Anna Müller",
                  "comment": "New corporate website",
                  "startDate": "2026-11-01",
                  "endDate": "2027-02-28",
                  "projectEmployees": [
                    { "id": 1, "employeeId": 2, "qualificationID": 2 },
                    { "id": 2, "employeeId": 12, "qualificationID": 3 }
                  ]
                }
                """)))
    @ApiResponse(responseCode = "404", description = "Project or employee not found", content = @Content(
            mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(value = """
                {
                  "detail": "Employee with ID 99 not found",
                  "instance": "/projects/1/employees",
                  "status": 404,
                  "title": "Not found"
                }
                """)))
    @ApiResponse(responseCode = "400", description = "Invalid request: missing employeeId or qualificationID", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "The employee is assigned to another project during that period, or already assigned to this project, or does not have the required qualification", content = @Content(
            mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class), examples = @ExampleObject(value = """
                {
                  "detail": "The Employee 12 holds the qualification with the ID 5 not!",
                  "instance": "/projects/1/employees",
                  "status": 409,
                  "title": "Lack of Qualifications"
                }
                """)))
    @ApiResponse(responseCode = "503", description = "The Employee Service is unavailable.", content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping("/{projectId}/employees")
    public ResponseEntity<ProjectDTO> addEmployeeToProject(
            @PathVariable Long projectId,
            @RequestBody @Valid CreateProjectEmployeeDTO dto) {

        ProjectDTO updatedProject =
                service.addEmployeeToProject(projectId, dto);

        return ResponseEntity.ok(updatedProject);
    }

}
