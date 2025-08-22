package com.user.producer.testcontainers;

import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static com.user.producer.testcontainers.Images.POSTGRESQL_CONTAINER_IMAGE;

@Testcontainers
@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@ActiveProfiles("it")
public class PostgreSQLTestcontainers {

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(POSTGRESQL_CONTAINER_IMAGE)
            .withDatabaseName("consumer-db")
            .withUsername("ronaldo")
            .withPassword("ronaldo")
            .withInitScript("init.sql") // Busca automaticamente em src/test/resources/
            .withStartupTimeout(Duration.ofMinutes(3))
            .withConnectTimeoutSeconds(60);

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        // Configurações do PostgreSQL
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    @BeforeEach
    void setUp(@Autowired Flyway flyway) {
        postgresContainer.start();
        flyway.clean();
        flyway.migrate();
    }

}
