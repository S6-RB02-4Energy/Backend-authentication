package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interface for the user service
 *
 * @class UserInterface
 */
public interface UserInterface {
        List<UserEntity> findAll();
        List<UserEntity> findAllByRole(Roles role);
        UserEntity save(UserEntity user);
        Optional<UserEntity> findById(UUID id);
        void deleteById(UUID id);
        void deleteAll();
        Optional<UserEntity> findByUsername(String username);
        Boolean existsByUsername(String username);
        Boolean existsByEmail(String email);
        Optional<UserEntity> findByEmail(String email);
}
