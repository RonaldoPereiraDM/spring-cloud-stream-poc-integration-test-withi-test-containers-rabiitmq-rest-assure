package com.user.consumer.listerner;

import com.user.consumer.dto.UserEventDto;
import com.user.consumer.models.User;
import com.user.consumer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

//@Configuration(proxyBeanMethods = false)
public class UserEventListenerConfig {

    private static final Logger log = LoggerFactory.getLogger(UserEventListenerConfig.class);

    //@Bean
    public Consumer<UserEventDto> userEventListener(UserService userService) {
        return dto -> {
            User user = dto.convertToUserModel();
            try {
                userService.save(user);
                log.info("[LISTENER] user {} saved.", user.getUserId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

}