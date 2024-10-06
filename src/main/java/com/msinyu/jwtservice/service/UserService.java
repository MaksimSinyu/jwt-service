package com.msinyu.jwtservice.service;

import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Registers a new user.
     *
     * @param user User to register.
     * @return Registered user.
     */
    @Transactional
    public User registerUser(User user) {
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
     * Updates a user's password hash and password vectors.
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
        userRepository.save(user);
    }

}
