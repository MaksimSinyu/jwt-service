package com.msinyu.jwtservice.controller;

import com.msinyu.jwtservice.dto.request.LoginRequest;
import com.msinyu.jwtservice.dto.request.RegisterRequest;
import com.msinyu.jwtservice.dto.response.ApiResponse;
import com.msinyu.jwtservice.dto.response.SuccessResponse;
import com.msinyu.jwtservice.dto.response.TokenResponse;
import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.service.PasswordService;
import com.msinyu.jwtservice.service.UserService;
import com.msinyu.jwtservice.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user.
     *
     * @param registerRequest Registration request data.
     * @return Success message or error message.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<SuccessResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("Username already exists.", null));
        }

        String hashedPassword = passwordService.hashPassword(registerRequest.getPassword());
        String passwordVector;
        try {
            passwordVector = passwordService.generatePasswordVector(hashedPassword);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error generating password vector.", null));
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(hashedPassword);
        try {
            passwordService.addPasswordHistory(user, hashedPassword, passwordVector);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("Error adding password history.", null));
        }

        userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("User registered successfully.", new SuccessResponse("User registered successfully.")));
    }

    /**
     * Authenticates a user and issues a JWT token.
     *
     * @param loginRequest Login request data.
     * @return JWT token or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            return userService.findByUsername(loginRequest.getUsername())
                    .map(user -> {
                        if (passwordService.verifyPassword(loginRequest.getPassword(), user.getPasswordHash())) {
                            String token = jwtUtil.generateToken(user);
                            ApiResponse<TokenResponse> successResponse = new ApiResponse<>(
                                    "Login successful.",
                                    new TokenResponse(token)
                            );
                            return ResponseEntity.ok(successResponse);
                        } else {
                            ApiResponse<TokenResponse> errorResponse = new ApiResponse<>(
                                    "Invalid credentials.",
                                    null
                            );
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
                        }
                    })
                    .orElseGet(() -> {
                        ApiResponse<TokenResponse> orElseResponse = new ApiResponse<>(
                                "Invalid credentials.",
                                null
                        );
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(orElseResponse);
                    });
        } catch (Exception e) {
            ApiResponse<TokenResponse> exceptionResponse = new ApiResponse<>(
                    "An error occurred during login.",
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
        }
    }

    /**
     * Changes the user's password.
     *
     * @param username    Username of the user.
     * @param newPassword New password.
     * @return Success message or error message.
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<SuccessResponse>> changePassword(
            @RequestParam String username,
            @RequestParam String newPassword) {
        return userService.findByUsername(username)
                .map(user -> {
                    String hashedPassword = passwordService.hashPassword(newPassword);
                    String passwordVector;
                    try {
                        passwordVector = passwordService.generatePasswordVector(hashedPassword);
                        userService.updateUserPassword(user, hashedPassword, passwordVector);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse<SuccessResponse>("Error updating password.", null));
                    }
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ApiResponse<>(
                                    "Password updated successfully.",
                                    new SuccessResponse("Password updated successfully.")
                            ));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<SuccessResponse>(
                        "User not found.",
                        null
                )));
    }

}
