package de.szut.pms.project;


import de.szut.pms.projectemployee.ProjectEmployee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bezeichnung;

    @Column(name = "verantwortlicher_mitarbeiter_id", nullable = false)
    private Long verantwortlicherMitarbeiterId;

    @Column(name = "kunden_id", nullable = false)
    private Long kundenId;

    @Column(name = "kunden_zustaendige_person", nullable = false)
    private String kundenZustaendigePerson;

    @Column(length = 2000) // Weil Kommentare sehr lang sein können
    private String kommentar;

    @Column(nullable = false)
    private LocalDate startdatum;

    @Column(nullable = false)
    private LocalDate enddatum;

    // cascade + orphanRemoval: Beim Löschen des Projekts verschwinden die Zuordnungen,
    // die Mitarbeiter sind danach wieder zuweisbar.
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectEmployee> mitarbeiter = new ArrayList<>();

}
