package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.entity.UserEntity;
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

import static com.Energy.BasicSpringAPI.service.AuthenticationFilter.doHashing;

@RestController
@RequestMapping("auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationFilter authenticationFilter;

    @Autowired
    private UserService userService;

    @PermitAll
    @PostMapping(value = "/login")
    public ResponseEntity authenticate(HttpServletResponse response, @RequestBody String body) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        System.out.println(body);
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String userName = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        System.out.println(userName);
        System.out.println(password);

        Optional<UserEntity> user = authService.getUser(userName, password);
        if (user.isEmpty()){
            return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
        }
        else {
            String userId = Long.toString(user.get().id);
            String token = authenticationFilter.createJWT(userId, user.get().email, user.get().username, -1);
            if (authenticationFilter.validateToken(token)) {
                return new ResponseEntity<>(token, HttpStatus.OK);

            }
            else {
                return new ResponseEntity<>("The email or password is wrong", HttpStatus.UNAUTHORIZED);
            }
        }
    }


    @PostMapping(value = "/verifyToken")
    public ResponseEntity VerifyToken(@RequestBody String body){
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String token = tokenizer.nextToken();

        if (authenticationFilter.validateToken(token)) {
            return new ResponseEntity<>("The token is valid", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("The token is invalid", HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/register")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity CreateUser(@RequestBody UserEntity user) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        try {
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

            if(user.role == null){
                return ResponseEntity
                        .badRequest()
                        .body("Error: Role is not valid!");
            }
            user.password = doHashing(user.password);
            return new ResponseEntity<>(userService.save(user), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
