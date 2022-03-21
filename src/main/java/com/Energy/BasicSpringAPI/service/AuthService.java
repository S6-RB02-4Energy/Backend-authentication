package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    @Autowired
    private AuthenticationFilter authenticationFilter;


    public Optional<UserEntity> getUser(String userName, String password) throws SQLException, URISyntaxException, NoSuchAlgorithmException {

        try {
            if(userRepository.existsByUsername(userName)){
                String encryptedPassword = authenticationFilter.doHashing(password);
                Optional<UserEntity> user = findByUsername(userName);

                if (user.get().password.equals(encryptedPassword)){
                    return user;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
