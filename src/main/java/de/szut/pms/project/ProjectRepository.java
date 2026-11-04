package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// Repository for accessing project data.
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Finds all projects assigned to a specific employee.
    @Query("""
           SELECT p FROM Project p
           JOIN p.employee pe
           WHERE pe.employeeId = :employeeId
           """)
    List<Project> findByEmployeeId(@Param("employeeId") Long employeeId);
}