package de.szut.pms.project;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {this.service = service;}

    @Operation(summary = "Delete a project")
    @ApiResponse(responseCode = "204", description = "Project successfully deleted")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @ApiResponse(responseCode = "401", description = "Invalid JWT")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build(); //mit .ok() würde 200 returnt
    }


}
