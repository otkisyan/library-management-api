package org.example.testcontainers;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@TestConfiguration
public class TestKeycloakConfig {

    static final  KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer();

    static {
        KEYCLOAK_CONTAINER.start();
    }
}
