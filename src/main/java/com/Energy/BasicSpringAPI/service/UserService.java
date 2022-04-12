package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.DTO.UserInfoDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


/**
 * Handles Users CRUD Operations and checks for username and email duplication
 *
 * @class UserService
 */
@Service
public class UserService implements UserInterface{
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> findAllByRole(Roles role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    public UserEntity save(UserEntity userEntity){
    return userRepository.save(userEntity);

    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Generates confirmationcode consisting out of 6 numbers
     * @return confirmationcode as String
     */
    public String getRandomConfirmationCode(){
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    /**
     * Checks if the confirmationCode matches. If true updates the current logged-in user.
     * @param confirmationCode from frontend given by the logged-in user
     * @param userId of current logged-in user
     * @return boolean
     */
    @Async
    public Boolean checkEmailConfirmationCode(String confirmationCode, String userId){
        UserEntity currentUser =  this.userRepository.findById(UUID.fromString(userId)).get();

        if (currentUser.confirmationCode != null  && (currentUser.confirmationCode.equals(confirmationCode))){ //maybe check better with equals?
            currentUser.emailConfirmed = true;
            currentUser.confirmationCode = null;
            this.save(currentUser);
            return  true;
        }
        return false;
    }

    /**
     * This method is used to get all user information for backend checks.
     * @param userId
     * @return UserEntity
     */
    public UserEntity getUserById(UUID userId){
        return this.userRepository.findById(userId).get();
    }

    /**
     * This method is used to get user information for the frontend.
     * @param userId
     * @return userInfoDto without password and confirmation-code.
     */
    public UserInfoDto getUserInfo(UUID userId){
        UserEntity user =  this.userRepository.findById(userId).get();
        return new UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getEmailConfirmed()
        );
    }

    /**
     * Saves user and returns user information for frontend.
     * @param user
     * @return UserInfoDto without password and confirmation-code.
     */
    public UserInfoDto saveUser(UserEntity user){
        UserEntity createdUser =  this.userRepository.save(user);
        return new UserInfoDto(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getRole(),
                createdUser.getEmailConfirmed()
        );
    }

}
