package org.example.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestMariaDbConfig {

    static final MariaDBContainer<?> MARIADB_CONTAINER =
            new MariaDBContainer<>(DockerImageName.parse("mariadb:latest"))
                    .withDatabaseName("test-db")
                    .withUsername("username")
                    .withPassword("password");

    static {
        MARIADB_CONTAINER.start();
    }

}