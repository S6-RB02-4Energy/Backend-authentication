package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.LoginDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.AuthService;
import com.Energy.BasicSpringAPI.service.AuthenticationFilter;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
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
    private AuthService authService;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private UserService userService;
    @Autowired
    private MailService mailService;

    @PermitAll
    @PostMapping(value = "/login")
    public ResponseEntity authenticate(@RequestBody LoginDto loginDto) {
        Optional<UserEntity> user = authService.getUser(loginDto.getEmail(), loginDto.getPassword());
        if (user.isEmpty()) {
            return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
        }
        String userId = String.valueOf(user.get().getId());
        String token = authenticationFilter.createJWT(user);
        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
    }

    @PostMapping(value = "/verifyToken")
    @PreAuthorize("#role == 'CONSUMER'")
    public ResponseEntity VerifyToken(@RequestBody String body, @RequestHeader String role) {
        System.out.println(role);
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String token = tokenizer.nextToken();

        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>("The token is valid", HttpStatus.OK);
        }
        return new ResponseEntity<>("The token is invalid", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/register")
    // TODO @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity CreateUser(@RequestBody UserEntity user) throws NoSuchAlgorithmException {
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity
                .badRequest()
                .body("Error: Username is already taken!");
        }

        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity
                .badRequest()
                .body("Error: Email is already in use!");
        }

        if (user.role == null) {
            return ResponseEntity
                .badRequest()
                .body("Error: Role is not valid!");
        }

        user.confirmationCode = this.userService.getRandomConfirmationCode();
        user.emailConfirmed = false;
        user.password = AuthenticationFilter.getBcryptHash(user.password);

        try {
            this.mailService.sendEmailConfirmation(user.email, user.username, user.confirmationCode);
            return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
