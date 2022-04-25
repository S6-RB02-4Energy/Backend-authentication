package com.Energy.BasicSpringAPI.controller;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.service.MailService;
import com.Energy.BasicSpringAPI.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.util.UUID;

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

    /**
     * Endpoint for confirming email address belongs to user.
     * @param userId of currentUser. We should get currentUser from backend, instead of sending userId from frontend.
     * @param confirmationCode
     * @return UserInfoDto without password and confirmation-code.
     */
    @PermitAll //TODO: user should be validated (with annotation for JWT-token).
    @PostMapping(value = "/confirmEmail/{userId}/{confirmationCode}")
    public ResponseEntity confirmEmail(@PathVariable(value="userId") String userId,
                                       @PathVariable(value="confirmationCode") String confirmationCode) {

        if (this.userService.checkEmailConfirmationCode(confirmationCode, userId)) {
            return new ResponseEntity<>(userService.getUserInfo(UUID.fromString(userId)), HttpStatus.OK);
        };

        return ResponseEntity
                .badRequest()
                .body("Confirmation failed");
    }


    //TODO: figure out how to get currentuser with JWT as a parameter, see example below
    /**
     * Resends a new confirmation-code to the email of the user.
     * @param userId
     * @return UserInfoDto without password and confirmation-code.
     */
    @PermitAll
    @PostMapping(value = "/resendConfirmationCode/{userId}")
    public ResponseEntity resendConfirmationCode(@PathVariable(value = "userId") String userId
                                                 /*@Currentuser user (EXAMPLE)*/){

        UserEntity currentUser = userService.getUserById(UUID.fromString(userId));
        currentUser.confirmationCode = userService.getRandomConfirmationCode();

        mailService.resendConfirmationCode(currentUser.email, currentUser.username, currentUser.confirmationCode);
        return new ResponseEntity<>(userService.saveUser(currentUser), HttpStatus.OK);
    }
}
