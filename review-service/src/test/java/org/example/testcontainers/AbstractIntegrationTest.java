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
        System.out.println(issuerUri);
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
        registry.add("spring.security.oauth2.client.registration.review-service.client-id",
                () -> "review-service");
        registry.add("spring.security.oauth2.client.registration.review-service.client-secret",
                () -> "C9ni1JNiGqXEKcwH2lHTi8vajwg62UuW");
        registry.add("spring.security.oauth2.client.registration.review-service.authorization-grant-type",
                () -> "client_credentials");
        registry.add("spring.security.oauth2.client.registration.review-service.provider",
                () -> "keycloak");
        registry.add("spring.security.oauth2.client.provider.keycloak.issuer-uri",
                () -> issuerUri);
        registry.add("spring.security.oauth2.client.provider.keycloak.token-uri",
                () -> issuerUri + "/protocol/openid-connect/token");
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
