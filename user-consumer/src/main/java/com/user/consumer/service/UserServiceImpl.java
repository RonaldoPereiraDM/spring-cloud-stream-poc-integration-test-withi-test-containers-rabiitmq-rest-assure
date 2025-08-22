package com.user.consumer.service;

import com.user.consumer.models.User;
import com.user.consumer.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Transactional
    @Override
    public User save(User user) throws RuntimeException {

        Optional<User> userModelOptional = findById(user.getUserId());

        if (userModelOptional.isPresent()){
            throw new RuntimeException("User already exists");
        }

        return userRepository.save(user);
    }
}
