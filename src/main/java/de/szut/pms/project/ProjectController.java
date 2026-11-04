package de.szut.pms.project;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }


    @Operation(summary = "Create a new project")
    @ApiResponse(
            responseCode = "201",
            description = "Projekt wurde angelegt",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectDTO.class),
                    examples = @ExampleObject(value = """
                        {
                          "id": 1,
                          "description": "Website-Relaunch",
                          "responsibleEmployeeId": 1,
                          "customerId": 5,
                          "customerServiceRepresentative": "Anna Müller",
                          "comment": "Neuer Webauftritt",
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
    @ApiResponse(
            responseCode = "400",
            description = "Der Rumpf war fehlerhaft",
            content = @Content(
                    mediaType = "application/problem+json",
                    examples = @ExampleObject(value = """
            {
              "detail": "Die Anfrage enthält ungültige Felder.",
              "instance": "/projects",
              "status": 400,
              "title": "Ungültige Eingabe",
              "errors": {
                "startDate": "Das Startdatum muss angegeben werden."
              }
            }
            """)
            )
    )
    @PostMapping
    public ResponseEntity<ProjectDTO> create(@RequestBody @Valid CreateProjectDTO dto) {
        ProjectDTO created = service.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get all employees from a project")
    @ApiResponse(responseCode = "200", description = "Gesuchtes Projekt mit Mitarbeiter erhalten")
    @ApiResponse(responseCode = "400", description = "Fehler in der Anfrage, möglich das die ID keine Zahl ist",content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "404", description = "Projekt nicht gefunden",content = @Content(mediaType = "application/problem+json"))
    @GetMapping(value = "{id}/employees", produces = "application/json")
    public ResponseEntity<ProjectEmployeesDTO> getEmployessFromProject(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findAllEmployeesOfProjectOneByID(id));
    }

    @Operation(summary = "Get all information about a project.")
    @ApiResponse(responseCode = "200", description = "Gesuchtes Projekt erhalten")
    @ApiResponse(responseCode = "400", description = "Fehler in der Anfrage, möglich das die ID keine Zahl ist",content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(responseCode = "404", description = "Projekt nicht gefunden",content = @Content(mediaType = "application/problem+json"))
    @GetMapping(value = "{id}", produces = "application/json")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id){
        return ResponseEntity.ok().body(service.findOneByID(id));
    }


    @Operation(summary = "Get a list of all projects")
    @ApiResponse(responseCode = "200", description = "Projekte erfolgreich abgerufen")
    @ApiResponse(responseCode = "500", description = "Interner Serverfehler")
    @GetMapping
    public ResponseEntity<List<ProjectOverviewDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Delete a project")
    @ApiResponse(responseCode = "204", description = "Project successfully deleted")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "401", description = "Invalid JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build(); //mit .ok() würde 200 returnt
    }

    @Operation(summary = "Remove an employee from project")
    @ApiResponse(responseCode = "200", description = "Employee successfully removed")
    @ApiResponse(responseCode = "404", description = "Projekt(Id) existiert nicht oder Mitarbeiter ist dem Projekt nicht zugeordnet")
    @ApiResponse(responseCode = "401", description = "Invalider JWT")
    @DeleteMapping("/{projectId}/employees/{employeeId}")
    public ResponseEntity<ProjectEmployeeRemoveDTO> removeEmployee(@PathVariable Long projectId, @PathVariable Long employeeId){
        ProjectEmployeeRemoveDTO projectEmployeeRemoveDTO = service.removeEmployee(projectId, employeeId);
        return ResponseEntity.ok(projectEmployeeRemoveDTO);
    }

    @Operation(summary = "Update the details of a project")
    @ApiResponse(
            responseCode = "200",
            description = "Projekt erfolgreich aktualisiert"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Ungültige Eingabedaten"
    )
    @ApiResponse(
            responseCode = "404",
            description = "Projekt oder referenzierte Ressource nicht gefunden"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProjectDTO dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }

}
