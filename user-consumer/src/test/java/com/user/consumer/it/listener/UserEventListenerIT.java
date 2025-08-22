package com.user.consumer.it.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.consumer.dto.UserEventDto;
import com.user.consumer.models.User;
import com.user.consumer.repository.UserRepository;
import com.user.consumer.testContainers.BaseIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.messaging.Message;

import java.time.Duration;
import java.util.Optional;

import static com.user.consumer.utils.ResourceUtils.getContentFromResource;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.messaging.support.MessageBuilder.withPayload;

class UserEventListenerIT extends BaseIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Should receive event when user is create!")
    public void shouldReceiveEventWhenUserIsCreated() throws Exception {
        // Given
        String jsonPayload = getContentFromResource("/json/correct/register_user.json");
        UserEventDto userEventDto = objectMapper.readValue(jsonPayload, UserEventDto.class);
        User userExpected = userEventDto.convertToUserModel();

        rabbitTemplate.convertAndSend(Exchanges.USER_EVENT_EXCHANGE.getExchangeName(), Queues.USER_EVENT_MS_COURSE.getQueueName(), jsonPayload);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<User> userOptional = userRepository.findById(userExpected.getUserId());
            User user = userOptional.get();
            assertThat(user.getUserId(), is(equalTo(userExpected.getUserId())));
        });

        // Then - Verificar que o listener processou a mensagem
//        Optional<User> userOptional = userRepository.findById(userExpected.getUserId());
//        User user = userOptional.get();
//
//        assertThat(user.getUserId(), is(equalTo(userExpected.getUserId())));
//        assertThat(user.getUserName(), is(userExpected.getUserName()));
//        assertThat(user.getEmail(), is(userExpected.getEmail()));
//        assertThat(user.getFullName(), is(userExpected.getFullName()));
//        assertThat(user.getUserStatus(), is(userExpected.getUserStatus()));
//        assertThat(user.getUserType(), is(userExpected.getUserType()));
//        assertThat(user.getPhoneNumber(), is(userExpected.getPhoneNumber()));
    }

}