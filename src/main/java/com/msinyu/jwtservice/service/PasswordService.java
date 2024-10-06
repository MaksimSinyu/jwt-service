package com.msinyu.jwtservice.service;

import com.msinyu.jwtservice.model.PasswordHistory;
import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.util.MathUtil;
import com.msinyu.jwtservice.util.secure.SecureMatrix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.zip.Deflater;

/**
 * Service for handling password operations.
 */
@Service
public class PasswordService {

    private static final int LOG_ROUND = 12;
    private static final SecureRandom random = new SecureRandom();
    private static final int MAX_PASSWORD_HISTORY = 5; // Limit to last 5 passwords

    @Autowired
    private MathUtil mathUtil;

    /**
     * Hashes a plain text password using BCrypt.
     *
     * @param password Plain text password.
     * @return Hashed password.
     */
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUND, random));
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param password        Plain text password.
     * @param hashedPassword Hashed password.
     * @return True if matches, else false.
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /**
     * Generates a transformed vector from the password hash.
     *
     * @param passwordHash Hashed password.
     * @return Transformed vector as a base64 string.
     * @throws Exception If encryption fails.
     */
    public String generatePasswordVector(String passwordHash) throws Exception {
        SecureMatrix secureMatrix = mathUtil.generateSecureMatrix(passwordHash);
        SecureMatrix transformedMatrix = mathUtil.nonLinearTransform(secureMatrix);
        SecureMatrix derivativeMatrix = mathUtil.calculateDerivative(transformedMatrix);
        String encryptedBinary = mathUtil.matrixToBinaryArray(derivativeMatrix);

        byte[] input = encryptedBinary.getBytes("UTF-8");
        byte[] output = new byte[input.length];
        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        int compressedDataLength = deflater.deflate(output);

        return Base64.getEncoder().encodeToString(output);
    }

    /**
     * Adds a new password history record to the user and maintains the history size.
     *
     * @param user            The user whose password is being updated.
     * @param hashedPassword  The new hashed password.
     * @param passwordVector  The generated password vector.
     * @throws Exception If encryption fails.
     */
    @Transactional
    public void addPasswordHistory(User user, String hashedPassword, String passwordVector) throws Exception {
        PasswordHistory history = new PasswordHistory();
        history.setPasswordHash(hashedPassword);
        history.setPasswordVector(passwordVector);
        user.addPasswordHistory(history);

        if (user.getPasswordHistories().size() > MAX_PASSWORD_HISTORY) {
            PasswordHistory oldest = user.getPasswordHistories().get(user.getPasswordHistories().size() - 1);
            user.removePasswordHistory(oldest);
        }
    }

}
