package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> getUser(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            Optional<UserEntity> user = userRepository.findByEmail(email);
            if (user.isPresent() && AuthenticationFilter.isPasswordValid(password, user.get().getPassword())) {
                return user;
            }
            return Optional.empty();
        }
        return Optional.empty();
    }
}
