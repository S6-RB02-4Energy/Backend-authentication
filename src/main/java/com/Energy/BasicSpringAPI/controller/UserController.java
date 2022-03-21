package com.Energy.BasicSpringAPI.controller;


import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles Users CRUD Operations
 *
 * @class UserController
 */
@RestController
@RequestMapping("/user")
//TODO After JWT assign roles that can access the functions
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity getAllUsers() {
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
    @GetMapping("/all/{role}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity getAllUsersByRole(@PathVariable Roles role) {
        try{
            return new ResponseEntity<>(userService.findAllByRole(role), HttpStatus.OK);
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
    @GetMapping("/{id}")
    public ResponseEntity getUserById(@PathVariable String id) {
        try{
            return new ResponseEntity<>(userService.findById(Integer.parseInt(id)).get(), HttpStatus.OK);
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
    @GetMapping("/{email}")
    public ResponseEntity getUserByEmail(@PathVariable String email) {
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
    @GetMapping("/{username}")
    public ResponseEntity getUserByUsername(@PathVariable String username) {
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
    public ResponseEntity updateUser(@RequestBody UserEntity body, @RequestBody Long userId) {
        try{
            // If the ORM finds user with existing id, it just changes the different columns
            if(Objects.equals(body.id, userId)){
                return new ResponseEntity<>(userService.save(body), HttpStatus.OK);

            }else{
                return new ResponseEntity<>("Unauthorized to change other user", HttpStatus.UNAUTHORIZED);

            }
        }
        catch (Exception e){
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
    public ResponseEntity deleteUserByUserId(@PathVariable Long id, @RequestBody Long userId) {
        try{
            Roles role = this.userService.findById(userId).get().role;
            if(!Objects.equals(id, userId) && role != Roles.ADMIN){
                return new ResponseEntity<>("Cannot delete another user, unless being an admin", HttpStatus.FORBIDDEN);
            }
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity deleteAllUsers(@RequestBody Long userId) {
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