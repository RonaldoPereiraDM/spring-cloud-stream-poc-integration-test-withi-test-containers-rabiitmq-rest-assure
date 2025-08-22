package com.user.consumer.functions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.consumer.dto.UserEventDto;
import com.user.consumer.models.User;
import com.user.consumer.repository.UserRepository;
import com.user.consumer.testContainers.BaseIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Optional;

import static com.user.consumer.utils.ResourceUtils.getContentFromResource;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class UserEventListenerIT extends BaseIT {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    @DisplayName("Should receive event when user is create!")
    public void shouldReceiveEventWhenUserIsCreated() throws Exception {
        // Given
        String jsonPayload = getContentFromResource("/json/correct/register_user.json");
        UserEventDto userEventDto = objectMapper.readValue(jsonPayload, UserEventDto.class);
        User userExpected = userEventDto.convertToUserModel();

        //when
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.convertAndSend("ead.user.event", "ead.user.created", userEventDto);

        //then
        await().atMost(Duration.ofSeconds(8)).untilAsserted(() -> {
            Optional<User> userOptional = userRepository.findById(userExpected.getUserId());
            User user = userOptional.get();
            assertThat(user.getUserId(), is(equalTo(userExpected.getUserId())));
            assertThat(user.getUserName(), is(equalTo(userExpected.getUserName())));
            assertThat(user.getEmail(), is(equalTo(userExpected.getEmail())));
            assertThat(user.getFullName(), is(equalTo(userExpected.getFullName())));
            assertThat(user.getUserStatus(), is(equalTo(userExpected.getUserStatus())));
            assertThat(user.getUserType(), is(equalTo(userExpected.getUserType())));
            assertThat(user.getPhoneNumber(), is(equalTo(userExpected.getPhoneNumber())));
            }
        );
    }

}