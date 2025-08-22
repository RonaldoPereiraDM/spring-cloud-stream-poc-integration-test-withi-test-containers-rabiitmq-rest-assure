package com.user.consumer.testContainers;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.user.consumer.it.listener.Queues;
import io.restassured.RestAssured;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
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

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static com.user.consumer.it.listener.Exchanges.USER_EVENT_EXCHANGE;
import static com.user.consumer.testContainers.Images.POSTGRESQL_CONTAINER_IMAGE;
import static com.user.consumer.testContainers.Images.RABBITMQ_CONTAINER_IMAGE;
import static com.user.consumer.utils.TestProfiles.IT;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles(IT)
//@Import(TestChannelBinderConfiguration.class)
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
    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(RABBITMQ_CONTAINER_IMAGE)
//            .withExchange(USER_EVENT_EXCHANGE.getExchangeName(), "topic")
//            .withQueue(Queues.USER_EVENT_MS_COURSE.getQueueName())
//            .withBinding(USER_EVENT_EXCHANGE.getExchangeName(), Queues.USER_EVENT_MS_COURSE.getQueueName());
            ;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        // Configurações do PostgreSQL
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.cloud.stream.defaultBinder", () -> "rabbit");
        registry.add("spring.rabbitmq.addresses", rabbitMQContainer::getAmqpUrl);

        // Configurações do Flyway (mesmas do datasource)
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgresContainer::getUsername);
        registry.add("spring.flyway.password", postgresContainer::getPassword);

        // Configurações do RabbitMQ
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getFirstMappedPort);

    }

//    private RabbitMQContainer container;
//    private AMQP.BasicProperties properties;
//    private Channel channel;

    @BeforeEach
    void setUp(@Autowired Flyway flyway) throws IOException, TimeoutException {

//        ConnectionFactory factory = createConnectionFactory();
//        Connection connection = factory.newConnection();
//        properties = defineMessageProperties();
//        channel = connection.createChannel();

        // Configura MockMvc com Spring Security
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.port = port;
        RestAssured.basePath = "";

        flyway.clean();
        flyway.migrate();
    }

    public static AMQP.@NotNull BasicProperties defineMessageProperties() {
        return new AMQP.BasicProperties.Builder()
                .contentType("application/json")
                .build();
    }
//
//    public static ConnectionFactory createConnectionFactory() {
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost(rabbitMQContainer.getHost());
//        factory.setPort(rabbitMQContainer.getAmqpPort());
//        factory.setUsername(rabbitMQContainer.getAdminUsername());
//        factory.setPassword(rabbitMQContainer.getAdminPassword());
//        return factory;
//    }

}
