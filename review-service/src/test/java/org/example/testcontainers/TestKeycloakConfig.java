package org.example.testcontainers;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class TestKeycloakConfig {

    static final  KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer();

    static {
        KEYCLOAK_CONTAINER.start();
    }
}
