package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.LoginDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.AuthService;
import com.Energy.BasicSpringAPI.service.AuthenticationFilter;
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
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.UUID;

import static com.Energy.BasicSpringAPI.service.AuthenticationFilter.doHashing;

/**
 * Handles Authentication
 *
 * @class AuthController
 */
@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;

    @PermitAll
    @PostMapping(value = "/login")
    public ResponseEntity authenticate(HttpServletResponse response,
                                       @RequestBody LoginDto loginDto) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        Optional<UserEntity> user = authService.getUser(loginDto.getEmail(), loginDto.getPassword());
        if (user.isEmpty()){
            return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
        }
        String userId = String.valueOf(user.get().getId());
        String token = authenticationFilter.createJWT(userId, user.get().email, user.get().username, -1);
        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping(value = "/verifyToken")
    public ResponseEntity VerifyToken(@RequestBody String body) {
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String token = tokenizer.nextToken();

        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>("The token is valid", HttpStatus.OK);
        }
        return new ResponseEntity<>("The token is invalid", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/register")
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

        if(user.role == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Role is not valid!");
        }

        // gets confirmation code for user to confirm his/her email with
        user.confirmationCode = this.userService.getRandomConfirmationCode();
        user.emailConfirmed = false;
        user.password = AuthenticationFilter.doHashing(user.password);

        try {
            //TODO hash the password
            //user.setPassword(user.password);

            //send mail with confirmation-code to user
            this.mailService.sendEmailConfirmation(user.email, user.username, user.confirmationCode);

            //returning the saved user with confirmation-code and HTTP status 201 Created
            return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            //If there is an unexpected error sending Internal Server Error 500
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
