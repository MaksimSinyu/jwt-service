package com.msinyu.jwtservice.service;

import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * Service for managing users.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordService passwordService;

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int RANDOM_HASH_LENGTH = 48; // 48 bytes -> 64 characters in Base64

    /**
     * Generates a secure random hash.
     *
     * @return Base64 encoded random hash string.
     */
    public String generateRandomHash() {
        byte[] randomBytes = new byte[RANDOM_HASH_LENGTH];
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    /**
     * Registers a new user.
     *
     * @param user User to register.
     * @return Registered user.
     */
    @Transactional
    public User registerUser(User user) {
        user.setRandomHash(generateRandomHash());
        return userRepository.save(user);
    }

    /**
     * Finds a user by username, eagerly loading password histories.
     *
     * @param username Username to search.
     * @return Optional containing the user if found.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsernameWithPasswordHistories(username);
    }

    /**
     * Updates a user's password hash and password vectors, and regenerates the random hash.
     *
     * @param user              User to update.
     * @param newPasswordHash   New password hash.
     * @param newPasswordVector New password vector.
     * @throws Exception If encryption fails.
     */
    @Transactional
    public void updateUserPassword(User user, String newPasswordHash, String newPasswordVector) throws Exception {
        user.setPasswordHash(newPasswordHash);
        passwordService.addPasswordHistory(user, newPasswordHash, newPasswordVector);
        user.setRandomHash(generateRandomHash());
        userRepository.save(user);
    }
}
