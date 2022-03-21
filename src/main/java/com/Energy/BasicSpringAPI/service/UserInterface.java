package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

public interface UserInterface {
        List<UserEntity> findAll();
        List<UserEntity> findAllByRole(Roles role);
        UserEntity save(UserEntity user) throws NoSuchAlgorithmException;
        Optional<UserEntity> findById(long id);
        void deleteById(long id);
        void deleteAll();
        Optional<UserEntity> findByUsername(String username);
        Boolean existsByUsername(String username);
        Boolean existsByEmail(String email);

}
