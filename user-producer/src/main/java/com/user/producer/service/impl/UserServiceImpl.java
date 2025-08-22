package com.user.producer.service.impl;

import com.user.producer.controller.dto.UserRecordDto;
import com.user.producer.models.UserModel;
import com.user.producer.publisher.UserEventPublisher;
import com.user.producer.publisher.dto.UserEventDto;
import com.user.producer.repository.UserRepository;
import com.user.producer.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.user.producer.enums.ActionType.CREATE;
import static com.user.producer.enums.UserStatus.ACTIVE;
import static com.user.producer.enums.UserType.USER;

@Component
public class UserServiceImpl implements UserService {

    private final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserEventPublisher userEventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            UserEventPublisher userEventPublisher
    ) {
        this.userRepository = userRepository;
        this.userEventPublisher = userEventPublisher;
    }

    @Override
    public UserModel save(UserRecordDto userRecordDto) {
        var userModel = new UserModel();
        BeanUtils.copyProperties(userRecordDto, userModel);
        userModel.setUserStatus(ACTIVE);
        userModel.setUserType(USER);
        userModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        userModel = userRepository.save(userModel);
        UserEventDto userEventDto = userModel.convertToUserEventDto(CREATE);
        userEventPublisher.publishUserEventCreate(userEventDto);
        logger.info("[SERVICE] User saved with id {}", userModel.getUserId());
        return userModel;
    }

}
