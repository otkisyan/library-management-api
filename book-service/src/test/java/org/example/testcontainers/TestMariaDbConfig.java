package org.example.testcontainers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
        System.setProperty("spring.datasource.url", MARIADB_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", MARIADB_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", MARIADB_CONTAINER.getPassword());
        System.setProperty("spring.datasource.driver-class-name", "org.mariadb.jdbc.Driver");
    }


}