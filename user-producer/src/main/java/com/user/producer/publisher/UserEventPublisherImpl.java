package com.user.producer.publisher;

import com.user.producer.configs.AppProperties;
import com.user.producer.publisher.dto.UserEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static com.user.producer.publisher.BindingDestination.USER_EVENT_DESTINATION;
import static com.user.producer.publisher.EventRoutingKey.USER_CREATED;

@Component
public class UserEventPublisherImpl implements UserEventPublisher{

    private final Logger logger = LoggerFactory.getLogger(UserEventPublisherImpl.class);

    private final StreamBridge streamBridge;

    public UserEventPublisherImpl(
            StreamBridge streamBridge
    ) {
        this.streamBridge = streamBridge;
    }

    public void publishUserEventCreate(UserEventDto userEventDto) {
        Message<UserEventDto> message = MessageBuilder.withPayload(userEventDto)
                .setHeader("routingKey", USER_CREATED.getKey())
                .build();
        boolean isSent = sendToEventChannel(USER_EVENT_DESTINATION.getBindingName(), message);
    }

    private boolean sendToEventChannel(String userEventDestination, Message<UserEventDto> message){
        boolean isSent = streamBridge.send(userEventDestination, message);
        logger.info("[PUBLISHER] User send to events channel: {}", isSent);
        return isSent;
    }

}