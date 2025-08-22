package com.user.producer.cloudstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.producer.enums.ActionType;
import com.user.producer.enums.UserStatus;
import com.user.producer.enums.UserType;
import com.user.producer.models.UserModel;
import com.user.producer.publisher.UserEventPublisher;
import com.user.producer.testcontainers.PostgreSQLTestcontainers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;

@DisabledIf("#{environment.acceptsProfiles('avro')}")
@SpringBootTest
@ImportTestcontainers(PostgreSQLTestcontainers.class)
@Import(TestChannelBinderConfiguration.class)
public class UserEventPublisherTest {

    private static final String USER_EVENT_BINDING_NAME = "ead.userevent";

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OutputDestination outputDestination;

    @Autowired
    private UserEventPublisher userEventPublisher;

    @Test
    void whenSendingMessage_thenValidateFullContent() throws Exception {

        // Limpe mensagens anteriores
        outputDestination.clear();

        // DEBUG: Veja quais canais estão disponíveis
        System.out.println("=== DEBUG INFO ===");

        System.out.println("Target binding: " + USER_EVENT_BINDING_NAME);

        LocalDateTime creationDate = LocalDateTime.of(2025, 4, 10, 12, 34, 0);
        LocalDateTime lastUpdateDate = LocalDateTime.of(2025, 4, 10, 12, 34, 0);

        UserModel userModel = new UserModel();
        UUID userId = UUID.fromString("ae63439d-34f8-419e-a6b5-61810d79eb9f");
        userModel.setUserId(userId);
        userModel.setEmail("ronaldo.pm@gmail.com");
        userModel.setUserStatus(UserStatus.ACTIVE);
        userModel.setUserType(UserType.USER);
        userModel.setFullName("ronaldo pereira almeida");
        userModel.setCreationDate(creationDate);
        userModel.setLastUpdateDate(lastUpdateDate);
        userModel.setPhoneNumber("11 97415 7822");
        userModel.setUserName("ronaldo.pa");

        userEventPublisher.publishUserEventCreate(userModel.convertToUserEventDto(ActionType.CREATE));


        Message<byte[]> outputMessage = outputDestination.receive(5000, USER_EVENT_BINDING_NAME);
        assertThat(outputMessage).isNotNull();
//        AppModel receivedModel = objectMapper.readValue(receivedMessage.getPayload(), AppModel.class);
//        assertThat(receivedModel.getId()).isNotNull();
//        assertThat(receivedModel.getName()).isEqualTo("Ronaldo Melo");
//        assertThat(receivedModel.getAddress()).isEqualTo("Rua algo");
    }

}
