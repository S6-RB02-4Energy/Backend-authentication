package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
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


import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import static java.lang.Integer.parseInt;

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
    public UserEntity save(UserEntity userEntity) throws NoSuchAlgorithmException {
        String password = doHashing(userEntity.password);
        userEntity.password = password;
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

    @Override
    public Optional<UserEntity> findByEmailAnadPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public Optional<UserEntity> getUser(String userName, String password) throws SQLException, URISyntaxException, NoSuchAlgorithmException {

        try {
            if(userRepository.existsByUsername(userName)){
                String encryptedPassword = doHashing(password);
                Optional<UserEntity> user = findByUsername(userName);

                if (user.get().password.equals(encryptedPassword)){
                    return user;
                }
                else {
                    return null;
                }
            }
            else {
                return null;
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
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

//    public UserEntity getUserFromToken(String token) {
//        Claims decoded = decodeJWT(token);
//
//        String id = decoded.getId();
//
//        UserEntity u = getUser(parseInt(id));
//
//        return u;
//    }
    public static String SECRET_KEY = "oeRaYY";

    public String createJWT(String id, String issuer, String subject, long ttlMillis) {

        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(1).toInstant()))
                .signWith(signatureAlgorithm, signingKey);

        //Builds the JWT and serializes it to a compact, URL-safe string

        return builder.compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new java.util.Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Expired or invalid JWT token");
        }
    }
    public Claims decodeJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

}
