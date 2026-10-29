package de.szut.pms.project;


import de.szut.pms.projectemployee.ProjectEmployee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    public void addEmployee(ProjectEmployee projectEmployee) {
        employee.add(projectEmployee);
        projectEmployee.setProject(this);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(name = "responsible_employee_id", nullable = false)
    private Long responsibleEmployeeId;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "customer_service_representative", nullable = false)
    private String customerServiceRepresentative;

    @Column(length = 2000) //Because comments can be very long
    private String comment;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    // cascade + orphanRemoval: When the project is deleted, the assignments disappear,
    // after that, the employees can be reassigned
    @OneToMany(
            mappedBy = "project",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ProjectEmployee> employee = new ArrayList<>();

}
