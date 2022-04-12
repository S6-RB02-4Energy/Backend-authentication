package com.Energy.BasicSpringAPI.service;

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

@Component
public class AuthenticationFilter {
    Dotenv dotenv = Dotenv.load();

    public static String getBcryptHash(String password) throws NoSuchAlgorithmException {
        PasswordEncoder passwordEncoder = getPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    private static PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(16);
    }


    private final String SECRET_KEY = dotenv.get("SECRET_KEY");

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
        boolean validation = false;
        try {
            Jws<Claims> claims  = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            validation = !claims.getBody().getExpiration().before(new java.util.Date());

        } catch (ExpiredJwtException e) {
            System.out.println(" Token expired ");
        } catch(Exception e){
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
