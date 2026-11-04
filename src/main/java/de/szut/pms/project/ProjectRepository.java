
package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

// Repository for accessing project data.
// JpaRepository provides basic database operations like saving, deleting and finding.
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Finds all projects assigned to a specific employee.
    @Query("""
           SELECT p FROM Project p
           JOIN p.employee pe
           WHERE pe.employeeId = :employeeId
           """)
    List<Project> findByEmployeeId(@Param("employeeId") Long employeeId);


    // Checks if an employee already has another project within the given time period.
    // Returns true if at least one overlapping project is found.
    @Query("""
       SELECT COUNT(pe) > 0
       FROM Project p
       JOIN p.employee pe
       WHERE pe.employeeId = :employeeId
       AND p.id <> :projectId
       AND p.startDate <= :endDate
       AND p.endDate >= :startDate
       """)
    boolean existsOverlappingProject(
            @Param("employeeId") Long employeeId,
            @Param("projectId") Long projectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
