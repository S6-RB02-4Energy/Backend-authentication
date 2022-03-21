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
//Not used do we need it?
    //    @Autowired
//    private AuthenticationFilter authenticationFilter;


    public Optional<UserEntity> getUser(String userName, String password) {

        try {
            if(userRepository.existsByUsername(userName)){
                String encryptedPassword = AuthenticationFilter.doHashing(password);
                Optional<UserEntity> user = findByUsername(userName);

                if (user.isPresent() && user.get().password.equals(encryptedPassword)){
                    return user;
                }
                else {
                    return Optional.empty();
                }
            }
            else {
                return Optional.empty();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}