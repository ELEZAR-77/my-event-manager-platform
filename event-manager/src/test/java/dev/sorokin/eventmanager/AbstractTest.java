package dev.sorokin.eventmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.sorokin.eventmanager.user.entity.enums.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Base64;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest
public class AbstractTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private UserUtils userUtils;

    private static volatile boolean isSetupDone = false;

    public static PostgreSQLContainer POSTGRESQL_CONTAINER =
            new  PostgreSQLContainer("postgres:16")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        if (!isSetupDone) {
            POSTGRESQL_CONTAINER.start();
            isSetupDone = true;
        }
    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
            registry.add("test.postgres.port", POSTGRESQL_CONTAINER::getFirstMappedPort);
    }

    @EventListener
    public void stopContainer(ContextStoppedEvent e) {
        log.info(e.getApplicationContext().toString());
        POSTGRESQL_CONTAINER.stop();
    }

    protected String getAuthorizationHeader(UserRole role) {
        return "Bearer " + userUtils.getJwtTokenWithRole(role);
    }
}
