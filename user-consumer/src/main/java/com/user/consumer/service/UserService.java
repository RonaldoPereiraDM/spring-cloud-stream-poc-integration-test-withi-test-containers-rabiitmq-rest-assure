package com.user.consumer.service;

import com.user.consumer.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {

    Optional<User> findById(UUID userId);

    User save(User user) throws Exception;

}