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


    /**
     * Getting all users
     *
     * @returns {ResponseEntity} List of UserEntity Objects
     * @memberof UserController
     */
    @GetMapping("/all")
    @PreAuthorize("#role == 'ADMIN'")
    public ResponseEntity<?> getAllUsers(@RequestHeader String role) {
        try{
            return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> getAllUsersByRole(@PathVariable Roles givenRole, @RequestHeader String role) {
        try{
            return new ResponseEntity<>(userService.findAllByRole(givenRole), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Getting user by id
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @GetMapping("id/{givenId}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<?> getUserById(@PathVariable UUID givenId, @RequestHeader String role ){
        try{
            return new ResponseEntity<>(userService.findById(givenId).get(), HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> getUserByEmail(@PathVariable String email, @RequestHeader String role ) {
        try{
            Optional<UserEntity> user = userService.findByEmail(email);
            if(user.isPresent()){
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>("There is no user with that email", HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Getting user by username
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, @RequestHeader String role ) {
        try{
            Optional<UserEntity> user = userService.findByUsername(username);
            if(user.isPresent()){
                return new ResponseEntity<>(user.get(), HttpStatus.OK);
            }
            return new ResponseEntity<>("There is no user with that username", HttpStatus.NOT_FOUND);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> updateUser(@RequestBody UserInfoDto userInfoDto, @RequestHeader String role, @RequestHeader String id) {
        UserEntity body = new UserEntity(userInfoDto);
        try {
            // If the ORM finds user with existing id, it just changes the different columns
            if (id.equals(body.getId().toString())) {
                UserEntity updated = userService.getUserById(body.getId());
                updated.email = (body.email == null) ? updated.email : body.email;
                updated.username = (body.username == null) ? updated.username : body.username;
                updated.password = (body.password == null) ? updated.password : AuthenticationFilter.getBcryptHash(body.password);
                return new ResponseEntity<>(userService.save(updated), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Internal service error", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while updating the user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Deleting user by id
     *
     * @returns {ResponseEntity} UserEntity
     * @memberof UserController
     */
    @DeleteMapping ("/delete/{id}")
    @PreAuthorize("#role == 'ADMIN' or #role =='CONSUMER' or #role =='LARGECONSUMER'or #role =='UTILITY'")
    public ResponseEntity<?> deleteUserByUserId(@PathVariable UUID id, @RequestHeader String role) {
        try{
            this.userService.deleteById(id);
            return new ResponseEntity<>("User Successfully Deleted", HttpStatus.OK);
        }
        catch (Exception e){
            logger.log(Level.SEVERE, e.getMessage());
            return new ResponseEntity<>("A problem occurred, while deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> deleteAllUsers(@RequestBody UUID userId, @RequestHeader String role) {
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
            return new ResponseEntity<>("A problem occurred, while fetching the data", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
