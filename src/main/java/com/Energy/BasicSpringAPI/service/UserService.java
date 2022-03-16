package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

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
    public Optional<UserEntity> findById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public void deleteById(long id) {
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

    public UserDto getUser(String email, String password) throws SQLException, URISyntaxException, NoSuchAlgorithmException {

        String encryptedPassword = doHashing(password);

        String passwordSaved = "52cbd20b20d8a47049a376309a2d73b7a6af2334c62dd05ca221fc2daf9ca525";
        if (encryptedPassword.equals(passwordSaved)){
            return new UserDto("test", email, encryptedPassword, Roles.ADMIN);
        }
        else {
            return null;
        }
    }


    public static String doHashing (String password) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        final byte[] hashbytes = digest.digest(
                password.getBytes(StandardCharsets.UTF_8));
        String sha3Hex = bytesToHex(hashbytes);
        
        return sha3Hex;
    }
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
        System.out.println(currentUser);

        if (currentUser.confirmationCode != null  && (currentUser.confirmationCode.equals(confirmationCode))){ //maybe check better with equals?
            currentUser.emailConfirmed = true;
            currentUser.confirmationCode = null;
            this.save(currentUser);
            return  true;
        }
        return false;
    }

}
