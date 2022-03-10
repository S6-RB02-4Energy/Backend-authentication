package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.StringTokenizer;

@RestController
@RequestMapping("auth")
public class AuthController {
//    @Autowired
//    private UserService userService;

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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OWNER')")
    public ResponseEntity<UserDto> CreateUser(HttpServletResponse response, @RequestBody UserDto user) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        try {
//            userService.save(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
