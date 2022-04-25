package com.Energy.BasicSpringAPI.service;

import com.Energy.BasicSpringAPI.entity.UserEntity;
import com.Energy.BasicSpringAPI.enumerators.Roles;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class AuthenticationFilter {
    Dotenv dotenv = Dotenv.load();

    public static String getBcryptHash(String password) throws NoSuchAlgorithmException {
        PasswordEncoder passwordEncoder = getPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private static PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static Boolean isPasswordValid(String currentPassword, String dbPassword) {
        Boolean isMatched = getPasswordEncoder().matches(currentPassword, dbPassword);
        return isMatched;
    }


    private final String SECRET_KEY = dotenv.get("SECRET_KEY");

    public String createJWT(Optional<UserEntity> user) {

        Map<String, Object> claims = new HashMap<>();
        // to add the role in the claims
        claims.put("role",user.get().getRole().toString());


        //The JWT signature algorithm we will be using to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //We will sign our JWT with our ApiKey secret
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder()
            .setClaims(claims)
            .setId(user.get().getId().toString())
            .setIssuedAt(now)
            .setSubject(user.get().getEmail())
            .setIssuer(user.get().getUsername())
            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(6).toInstant()))
            .signWith(signatureAlgorithm, signingKey);

        //Builds the JWT and serializes it to a compact, URL-safe string

        return builder.compact();
    }

    public boolean validateToken(String token) {
        boolean validation = false;
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            validation = !claims.getBody().getExpiration().before(new java.util.Date());

        } catch (ExpiredJwtException e) {
            System.out.println(" Token expired ");
        } catch (Exception e) {
            System.out.println(" Some other exception in JWT parsing ");
        }
        return validation;
    }

    public Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
            .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
            .parseClaimsJws(jwt).getBody();
    }
}
