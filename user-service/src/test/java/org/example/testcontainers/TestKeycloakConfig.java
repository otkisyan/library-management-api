package org.example.testcontainers;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@TestConfiguration
public class TestKeycloakConfig {

    private static Path findRealmFile() {
        Path dir = Paths.get(System.getProperty("user.dir"));

        for (int i = 0; i < 5; i++) {
            Path candidate = dir.resolve("docs/keycloak/test-realm-export.json");
            if (Files.exists(candidate)) {
                return candidate.toAbsolutePath();
            }
            dir = dir.getParent();
            if (dir == null) break;
        }
        throw new IllegalStateException("test-realm-export.json not found in parent directories");
    }

    private static final Path REALM_FILE_PATH = findRealmFile();

    static final  KeycloakContainer KEYCLOAK_CONTAINER =
            new KeycloakContainer("quay.io/keycloak/keycloak:26.3.2")
            .withCopyFileToContainer(
                    MountableFile.forHostPath(REALM_FILE_PATH.toString()),
                    "/opt/keycloak/data/import/test-realm-export.json");

//    static final  KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer("quay.io/keycloak/keycloak:26.3.2")
//            .withRealmImportFile("test-realm-export.json");

    static {
        KEYCLOAK_CONTAINER.start();
    }
}
