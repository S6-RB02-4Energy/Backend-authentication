package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
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
 * Confirms mail-address
 * Resend confirmation-code
 */
@RestController
@RequestMapping("mail")
public class MailController {

    private final UserService userService;
    private final MailService mailService;

    MailController(UserService userService, MailService mailService){
        this.userService = userService;
        this.mailService = mailService;
    }

    @PermitAll //TODO: user should be validated (with annotation for JWT-token).
    @PostMapping(value = "/confirmEmail/{userId}/{confirmationCode}")
    public ResponseEntity confirmEmail(@PathVariable(value="userId") String userId,
                                       @PathVariable(value="confirmationCode") String confirmationCode) {

        if (this.userService.checkEmailConfirmationCode(confirmationCode, userId)) {
            return new ResponseEntity<>(userService.findById(Long.parseLong(userId)), HttpStatus.OK);
        };

        return ResponseEntity
                .badRequest()
                .body("Confirmation failed");
    }


    //TODO: figure out how to get currentuser with JWT as a parameter, see example below
    @PermitAll
    @PostMapping(value = "/resendConfirmationCode/{userId}")
    public ResponseEntity resendConfirmationCode(@PathVariable(value = "userId") String userId
                                                 /*@Currentuser user (EXAMPLE)*/){
        UserEntity currentUser = userService.findById(Long.parseLong(userId)).get();
        currentUser.confirmationCode = userService.getRandomConfirmationCode();

        mailService.resendConfirmationCode(currentUser.email, currentUser.username, currentUser.confirmationCode);
        return new ResponseEntity<>(userService.save(currentUser), HttpStatus.OK);
    }
}
