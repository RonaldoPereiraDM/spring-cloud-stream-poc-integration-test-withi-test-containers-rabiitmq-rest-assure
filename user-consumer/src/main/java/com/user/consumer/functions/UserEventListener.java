package com.user.consumer.functions;

import com.user.consumer.dto.UserEventDto;
import com.user.consumer.models.User;
import com.user.consumer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

//@Component("userEventListener")
public class UserEventListener implements Consumer<Message<UserEventDto>> {

    private final Logger logger = LoggerFactory.getLogger(UserEventListener.class);

    private final UserService userService;

    public UserEventListener(UserService userService) {
        this.userService = userService;
    }

    private final User user = new User();

    @Override
    public void accept(Message<UserEventDto> userEventDtoMessage) {
        UserEventDto userEventDtoPayload = userEventDtoMessage.getPayload();
        User user = userEventDtoPayload.convertToUserModel();

        try {
            user = userService.save(user);
            logger.info("[LISTENER] user {} saved.", user.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}