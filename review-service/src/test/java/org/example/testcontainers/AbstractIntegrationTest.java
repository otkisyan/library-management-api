package org.example.testcontainers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest
@Import({
        TestKafkaConfig.class,
        TestKeycloakConfig.class,
        TestMariaDbConfig.class
})
@ActiveProfiles("ci")
public class AbstractIntegrationTest {

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",
                TestMariaDbConfig.MARIADB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username",
                TestMariaDbConfig.MARIADB_CONTAINER::getUsername);
        registry.add("spring.datasource.password",
                TestMariaDbConfig.MARIADB_CONTAINER::getPassword);
        registry.add(
                "spring.kafka.bootstrap-servers",
                TestKafkaConfig.KAFKA_CONTAINER::getBootstrapServers);
        registry.add(
                "spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> TestKeycloakConfig.KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/master");
    }

    @Test
    void mariaDbIsRunning(){
        Assertions.assertTrue(TestMariaDbConfig.MARIADB_CONTAINER.isRunning());
    }

    @Test
    void KeycloakIsRunning(){
        Assertions.assertTrue(TestKeycloakConfig.KEYCLOAK_CONTAINER.isRunning());
    }

    @Test
    void KafkaIsRunning(){
        Assertions.assertTrue(TestKafkaConfig.KAFKA_CONTAINER.isRunning());
    }
}
