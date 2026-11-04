package de.szut.pms.projectemployee;

import de.szut.pms.project.Project;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// An employee may appear only once in the same project: (project_id, employee_id) is unique.
// The same employee may appear in different projects. The database cannot check for time overlaps between projects; the service handles that (story “Prevent Time Overlaps”).

@Entity
@Table(name = "project_employee", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "employee_id"}))
@Getter
@Setter
@NoArgsConstructor
public class ProjectEmployee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Long qualificationID;
}