package com.Energy.BasicSpringAPI.service.services;

import com.Energy.BasicSpringAPI.DTO.UserDto;
import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import com.Energy.BasicSpringAPI.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

@Override
public UserEntity save(UserEntity userEntity){
    return userRepository.save(UserEntity);
}
//    public UserEntity save(UserEntity user){
//        if (user != null) {
//            return userRepository.save(user);
//        }else throw new NullPointerException();
//    }

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

}
