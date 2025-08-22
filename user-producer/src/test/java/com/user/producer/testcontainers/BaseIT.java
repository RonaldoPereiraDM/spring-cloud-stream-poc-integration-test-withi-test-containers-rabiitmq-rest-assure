package com.user.producer.testcontainers;

import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static com.user.producer.utils.TestProfiles.IT;
import static com.user.producer.testcontainers.Images.POSTGRESQL_CONTAINER_IMAGE;
import static com.user.producer.testcontainers.Images.RABBITMQ_CONTAINER_IMAGE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles(IT)
@Import(TestChannelBinderConfiguration.class)
public abstract class BaseIT {

//    @Autowired
//    WebApplicationContext context;

    @LocalServerPort
    private int port;

    @Container
    static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(POSTGRESQL_CONTAINER_IMAGE)
            .withDatabaseName("consumer-db")
            .withUsername("ronaldo")
            .withPassword("ronaldo")
            .withInitScript("init.sql") // Busca automaticamente em src/test/resources/
            .withStartupTimeout(Duration.ofMinutes(3))
            .withConnectTimeoutSeconds(60);

    @Container
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(RABBITMQ_CONTAINER_IMAGE);

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
        // Configura MockMvc com Spring Security
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "";

        flyway.clean();
        flyway.migrate();
    }

}
