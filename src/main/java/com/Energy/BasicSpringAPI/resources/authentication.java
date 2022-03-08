package com.Energy.BasicSpringAPI.resources;

import com.Energy.BasicSpringAPI.controller.UserController;

import com.Energy.BasicSpringAPI.models.User;
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
@RequestMapping("authentication")
public class authentication {

    @PermitAll
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity authenticate(HttpServletResponse response, @RequestBody String body) throws IOException, SQLException, URISyntaxException, NoSuchAlgorithmException {
        UserController userController = new UserController();
        System.out.println(body);
        final StringTokenizer tokenizer = new StringTokenizer(body, ":");
        final String email = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        System.out.println(email);
        System.out.println(password);

        User user = userController.getUser(email, password);
        if (user==null){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        else {
            return new ResponseEntity<>(user, HttpStatus.OK);

        }
    }

}
