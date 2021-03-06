package com.Energy.BasicSpringAPI.controller;


import com.Energy.BasicSpringAPI.DTO.UserInfoDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.service.AuthenticationFilter;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles Users CRUD Operations
 *
 * @class UserController
 */
@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;

    static final Logger logger = Logger.getLogger("com.Energy.AuthenticationAPI.UserController");
    static final String ERROR_MESSAGE = "A problem occurred, while fetching the data";
    static final String ERROR_NAME = "Error";


    /**
     * Getting all users
     *
     * @returns {ResponseEntity} List of UserEntity Objects
     * @memberof UserController
     */
    @GetMapping("/all")
    @PreAuthorize("#role == 'ADMIN'")
    public ResponseEntity<List<UserEntity>> getAllUsers(@RequestHeader String role) {
        try{
            return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(Collections.emptyList());
        }
    }
    /**
     * Getting all users by role
     *
     * @returns {ResponseEntity} List of UserEntity Objects
     * @memberof UserController
     */
    @GetMapping("/all/{givenRole}")
    @PreAuthorize("#role == 'ADMIN'")
    public ResponseEntity<List<UserEntity>> getAllUsersByRole(@PathVariable Roles givenRole, @RequestHeader String role) {
        try{
            return new ResponseEntity<>(userService.findAllByRole(givenRole), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(Collections.emptyList());        }
    }

    /**
     * Getting user by id
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @GetMapping("id/{givenId}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<UserEntity> getUserById(@PathVariable UUID givenId, @RequestHeader String role ){
        try{
            return new ResponseEntity<>(userService.findById(givenId).get(), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);
        }
    }

    /**
     * Getting user by email
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email, @RequestHeader String role ) {
        try{
            Optional<UserEntity> user = userService.findByEmail(email);
            if(user.isPresent()){
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, "No user with that email")
                .body(null);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);        }
    }

    /**
     * Getting user by username
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<UserEntity> getUserByUsername(@PathVariable String username, @RequestHeader String role ) {
        try{
            Optional<UserEntity> user = userService.findByUsername(username);
            if(user.isPresent()){
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, "No user with that username")
                .body(null);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);
        }
    }

    /**
     * Updating an existing user
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @PutMapping("/update")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<UserEntity> updateUser(@RequestBody UserInfoDto userInfoDto, @RequestHeader String role, @RequestHeader String id) {
        try {
            // If the ORM finds user with existing id, it just changes the different columns
            System.out.println(id + ' ' + userInfoDto.getId());
            if (id.equals(userInfoDto.getId().toString())) {
                UserEntity updated = userService.getUserById(userInfoDto.getId());
                updated.email = (userInfoDto.getEmail() == null) ? updated.email : userInfoDto.getEmail();
                updated.username = (userInfoDto.getUsername() == null) ? updated.username : userInfoDto.getUsername();
                updated.password = (userInfoDto.getPassword() == null) ? updated.password : AuthenticationFilter.getBcryptHash(userInfoDto.getPassword());
                return new ResponseEntity<>(userService.save(updated), HttpStatus.OK);
            } else {
                return ResponseEntity.badRequest()
                    .header(ERROR_NAME, ERROR_MESSAGE)
                    .body(null);            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);        }
    }

    /**
     * Deleting user by id
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @DeleteMapping ("/delete/{id}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<String> deleteUserByUserId(@PathVariable String id, @RequestHeader String role) {
        try{
            UUID userId = UUID.fromString(id);
            this.userService.deleteById(userId);
            return new ResponseEntity<>("User Successfully Deleted", HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);
        }
    }

    /**
     * Wiping Users Database
     *
     * @returns {ResponseEntity} HTTP Status 200 OK(Success) or 500 Internal Server Error(Fail)
     * @memberof UserController
     */
    //TODO not sure if it is smart to have a function wiping the user database
    @DeleteMapping("/wipeUsersDatabase")
    @PreAuthorize("#role == 'ADMIN'")
    public ResponseEntity<String> deleteAllUsers(@RequestBody UUID userId, @RequestHeader String role) {
        try{
            if(this.userService.findById(userId).get().role == Roles.ADMIN){
                this.userService.deleteAll();
                return new ResponseEntity<>("Successfully wiped the user Database", HttpStatus.OK);
            }else{
                return new ResponseEntity<>("This Endpoint is for admins only", HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return ResponseEntity.internalServerError()
                .header(ERROR_NAME, ERROR_MESSAGE)
                .body(null);        }
    }
}
