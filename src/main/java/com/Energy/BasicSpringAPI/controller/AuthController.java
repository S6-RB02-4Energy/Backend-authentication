package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.LoginDto;
import com.Energy.BasicSpringAPI.DTO.UserInfoDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.AuthService;
import com.Energy.BasicSpringAPI.service.AuthenticationFilter;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    static final String ERROR_NAME = "Error";

    @PermitAll
    @PostMapping(value = "/login")
    public ResponseEntity<String> authenticate(@RequestBody LoginDto loginDto) {
        Optional<UserEntity> user = authService.getUser(loginDto.getEmail(), loginDto.getPassword());
        if (user.isEmpty()) {
            return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
        }
        String token = authenticationFilter.createJWT(user);
        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        return ResponseEntity.status(401)
            .header(ERROR_NAME, "Wrong Credentials")
            .body(null);    }

    @PostMapping(value = "/verifyToken")
//    @PreAuthorize("#role == 'CONSUMER'")
    public ResponseEntity<String> verifyToken(@RequestBody String body, @RequestHeader String role) {
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String token = tokenizer.nextToken();

        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>("The token is valid", HttpStatus.OK);
        }
        return new ResponseEntity<>("The token is invalid", HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/register")
    public ResponseEntity<UserInfoDto> createUser(@RequestBody UserInfoDto userInfoDto) throws NoSuchAlgorithmException {
        UserEntity user = new UserEntity(userInfoDto);
        if (userService.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest()
                .header(ERROR_NAME, "username already exists")
                .body(null);
        }

        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest()
                .header(ERROR_NAME, "email already exists")
                .body(null);
        }

        if (user.role == null) {
            return ResponseEntity.badRequest()
                .header(ERROR_NAME, "role not valid")
                .body(null);
        }

        user.confirmationCode = this.userService.getRandomConfirmationCode();
        user.emailConfirmed = false;
        user.password = AuthenticationFilter.getBcryptHash(user.password);

        try {
//            this.mailService.sendEmailConfirmation(user.email, user.username, user.confirmationCode);
                return new ResponseEntity<>(userService.saveUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
