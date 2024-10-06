package com.msinyu.jwtservice.security;

import com.msinyu.jwtservice.model.PasswordHistory;
import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.util.EncryptionUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Utility class for JWT operations.
 */
@Component
public class JwtUtil {

    @Value("${jwt.service.key}")
    private String serviceKey;

    @Value("${jwt.token.expiration}")
    private long tokenExpiration;

    @Autowired
    private EncryptionUtil encryptionUtil;

    /**
     * Generates a JWT token for a user.
     *
     * @param user User for whom the token is generated.
     * @return JWT token string.
     */
    public String generateToken(User user) {
        String userKey = generateUserKey(user);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(SignatureAlgorithm.HS256, userKey.getBytes(StandardCharsets.UTF_8))
                .compact();
    }

    /**
     * Generates a unique key for the user by concatenating the service key with all of the user's password vectors
     * and applying a SHA-256 hash.
     *
     * @param user User object.
     * @return Concatenated and hashed key string.
     */
    private String generateUserKey(User user) {
        StringBuilder allVectors = new StringBuilder();
        for (PasswordHistory history : user.getPasswordHistories()) {
            allVectors.append(history.getPasswordVector());
        }
        String combined = serviceKey + allVectors.toString();
        return sha256(combined);
    }

    /**
     * Generates a SHA-256 hash of the input string.
     *
     * @param input Input string.
     * @return SHA-256 hashed string in hexadecimal format.
     */
    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            //TODO Consider better logic....
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    /**
     * Validates a JWT token.
     *
     * @param token JWT token string.
     * @param user  User to validate against.
     * @return True if valid, else false.
     */
    public boolean validateToken(String token, User user) {
        try {
            String userKey = generateUserKey(user);
            Jwts.parser().setSigningKey(userKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts the username from the token.
     *
     * @param token JWT token string.
     * @return Username.
     */
    public String extractUsername(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(serviceKey.getBytes(StandardCharsets.UTF_8))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
