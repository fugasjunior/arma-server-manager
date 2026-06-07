package cz.forgottenempire.servermanager.support;

import cz.forgottenempire.servermanager.installation.TestRunService;
import cz.forgottenempire.servermanager.support.fakes.FakeProcessFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("integrationtest")
@Import({FakeSteamApiConfig.class, MockMvcConfig.class})
public abstract class AbstractIntegrationTest {

    // Single container started once per JVM, shared across all test classes.
    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    // Replaced with a no-op: real TestRunService does live A2S network queries against the game server binary.
    @MockitoBean
    protected TestRunService testRunService;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected AuthTestHelper auth;

    @Autowired
    protected FakeProcessFactory fakeProcesses;

    @BeforeEach
    void resetBeforeEach() {
        fakeProcesses.reset();
        auth.invalidateSession();
    }

    protected Api api() {
        return new Api(mockMvc, auth::getSession);
    }
}
