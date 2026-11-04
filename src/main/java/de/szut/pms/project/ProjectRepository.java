package de.szut.pms.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("""
           SELECT p FROM Project p
           JOIN p.employee pe
           WHERE pe.employeeId = :employeeId
           """)
    List<Project> findByEmployeeId(@Param("employeeId") Long employeeId);
}