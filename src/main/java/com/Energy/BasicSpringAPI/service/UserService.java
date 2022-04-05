package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.DTO.UserInfoDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;


import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static com.Energy.BasicSpringAPI.service.AuthenticationFilter.doHashing;
import static java.lang.Integer.parseInt;

/**
 * Handles Users CRUD Operations and checks for username and email duplication
 *
 * @class UserService
 */
@Service
public class UserService implements UserInterface{
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<UserEntity> findAllByRole(Roles role) {
        return userRepository.findAllByRole(role);
    }

    @Override
    public UserEntity save(UserEntity userEntity){
    return userRepository.save(userEntity);

    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userRepository.deleteAll();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

//    public User getUserFromToken(String token) {
//        Claims decoded = decodeJWT(token);
//
//        String id = decoded.getId();
//
//        User u = getUser(parseInt(id));
//
//        return u;
//    }
//    public static String SECRET_KEY = "oeRaYY";
//
//    public String createJWT(String id, String issuer, String subject, long ttlMillis) {
//
//        //The JWT signature algorithm we will be using to sign the token
//        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//        long nowMillis = System.currentTimeMillis();
//        Date now = new Date(nowMillis);
//
//        //We will sign our JWT with our ApiKey secret
//        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
//        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
//
//        //Let's set the JWT Claims
//        JwtBuilder builder = Jwts.builder().setId(id)
//                .setIssuedAt(now)
//                .setSubject(subject)
//                .setIssuer(issuer)
//                .signWith(signatureAlgorithm, signingKey);
//
//        //Builds the JWT and serializes it to a compact, URL-safe string
//        return builder.compact();
//    }
//    public Claims decodeJWT(String jwt) {
//
//        //This line will throw an exception if it is not a signed JWS (as expected)
//        Claims claims = Jwts.parser()
//                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
//                .parseClaimsJws(jwt).getBody();
//        return claims;
//    }

    /**
     * Generates confirmationcode consisting out of 6 numbers
     * @return confirmationcode as String
     */
    public String getRandomConfirmationCode(){
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(999999);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }

    /**
     * Checks if the confirmationCode matches. If true updates the current logged in user.
     * @param confirmationCode from frontend given by the logged in user
     * @param userId of current logged in user
     * @return
     */
    @Async
    public Boolean checkEmailConfirmationCode(String confirmationCode, String userId){
        UserEntity currentUser =  this.userRepository.findById(UUID.fromString(userId)).get();

        if (currentUser.confirmationCode != null  && (currentUser.confirmationCode.equals(confirmationCode))){ //maybe check better with equals?
            currentUser.emailConfirmed = true;
            currentUser.confirmationCode = null;
            this.save(currentUser);
            return  true;
        }
        return false;
    }

    /**
     * This method is used to get all user information for backend checks.
     * @param userId
     * @return UserEntity
     */
    public UserEntity getUserById(UUID userId){
        return this.userRepository.findById(userId).get();
    }

    /**
     * This method is used to get user information for the frontend.
     * @param userId
     * @return userInfoDto without password and confirmation-code.
     */
    public UserInfoDto getUserInfo(UUID userId){
        UserEntity user =  this.userRepository.findById(userId).get();
        return new UserInfoDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getEmailConfirmed()
        );
    }

    /**
     * Saves user and returns user information for frontend.
     * @param user
     * @return UserInfoDto without password and confirmation-code.
     */
    public UserInfoDto saveUser(UserEntity user){
        UserEntity createdUser =  this.userRepository.save(user);
        return new UserInfoDto(
                createdUser.getId(),
                createdUser.getUsername(),
                createdUser.getEmail(),
                createdUser.getRole(),
                createdUser.getEmailConfirmed()
        );
    }

}
