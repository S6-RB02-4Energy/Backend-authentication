package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.StringTokenizer;


/**
 * Handles Authentication
 *
 * @class AuthController
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;


    /**
     * Authenticating user
     *
     * @returns {ResponseEntity}
     * @memberof AuthController
     */
    @PermitAll
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity authenticate(HttpServletResponse response, @RequestBody String body) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        UserService userService = new UserService();
        System.out.println(body);
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String email = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        System.out.println(email);
        System.out.println(password);

        UserDto userDto = userService.getUser(email, password);
        if (userDto ==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(userDto, HttpStatus.OK);

        }
    }

    /**
     * Registering a new User
     *
     * @returns {ResponseEntity} user/Bad Request with error message
     * @memberof AuthController
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity CreateUser(HttpServletResponse response, @RequestBody UserEntity user) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        //Checking if username is already in use
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        //Check if there is already user with that email
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        //Checking if role is null
        if(user.role == null){
            return ResponseEntity
                    .badRequest()
                    .body("Error: Role is not valid!");
        }

        // gets confirmation code for user to confirm his/her email with
        user.confirmationCode = this.userService.getRandomConfirmationCode();
        user.emailConfirmed = false;


        try {
            //TODO hash the password
            //user.setPassword(user.password);
            //After all the checks the user is saved in the database
            userService.save(user);
        } catch (Exception e) {
            //If there is an unexpected error sending Internal Server Error 500
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        //send mail with confirmation-code to user
        this.mailService.sendEmailConfirmation(user.email, user.username, user.confirmationCode);

        //returning the saved user with confirmation-code and HTTP status 201 Created
        return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
    }

}
