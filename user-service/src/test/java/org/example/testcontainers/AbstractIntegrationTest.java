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
public abstract class AbstractIntegrationTest {

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        String issuerUri = TestKeycloakConfig.KEYCLOAK_CONTAINER.getAuthServerUrl() + "/realms/test-realm";
        String issuerUriAdmin = TestKeycloakConfig.KEYCLOAK_CONTAINER.getAuthServerUrl() + "/admin/realms/test-realm";
        registry.add("spring.datasource.url",
                TestMariaDbConfig.MARIADB_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username",
                TestMariaDbConfig.MARIADB_CONTAINER::getUsername);
        registry.add("spring.datasource.password",
                TestMariaDbConfig.MARIADB_CONTAINER::getPassword);
        registry.add(
                "spring.kafka.bootstrap-servers",
                TestKafkaConfig.KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuerUri);
        registry.add("spring.security.oauth2.client.registration.user-service.client-id",
                () -> "user-service");
        registry.add("spring.security.oauth2.client.registration.user-service.client-secret",
                () -> "JGafu7SkhCviPslEvQQPFoOW35l6xCVB");
        registry.add("spring.security.oauth2.client.registration.user-service.authorization-grant-type",
                () -> "client_credentials");
        registry.add("spring.security.oauth2.client.registration.user-service.provider",
                () -> "keycloak");
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> issuerUri);
        registry.add("spring.security.oauth2.client.provider.keycloak.token-uri",
                () -> issuerUri + "/protocol/openid-connect/token");
        registry.add("keycloak.admin.base-url",
                () -> issuerUriAdmin);
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
