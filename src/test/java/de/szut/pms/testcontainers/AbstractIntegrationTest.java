package de.szut.pms.testcontainers;

import de.szut.pms.customer.CustomerClient;
import de.szut.pms.employee.EmployeeClient;
import de.szut.pms.project.ProjectRepository;
import de.szut.pms.project.ProjectService;
import de.szut.pms.projectemployee.ProjectEmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
/**
 * Startet einen echten Postgres-Container über Testcontainers und die volle
 * Anwendung. Zum Authentifizieren in Tests nicht Authentik ansprechen —
 * dafür gibt es {@code @WithMockUser}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = PostgresContextInitializer.class)
public class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected ProjectEmployeeRepository projectEmployeeRepository;

    @MockitoBean
    protected EmployeeClient employeeClient;

    @MockitoBean
    protected CustomerClient customerClient;

    @Autowired
    protected ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectEmployeeRepository.deleteAll();
        projectRepository.deleteAll();
    }
}
