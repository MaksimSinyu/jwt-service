package com.msinyu.jwtservice;

import com.msinyu.jwtservice.dto.request.LoginRequest;
import com.msinyu.jwtservice.dto.request.RegisterRequest;
import com.msinyu.jwtservice.dto.response.ApiResponse;
import com.msinyu.jwtservice.dto.response.SuccessResponse;
import com.msinyu.jwtservice.dto.response.TokenResponse;
import com.msinyu.jwtservice.model.User;
import com.msinyu.jwtservice.repository.UserRepository;
import com.msinyu.jwtservice.security.JwtUtil;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for the JWT Service Application.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
public class JwtServiceApplicationTests {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String baseUrl;

    @BeforeEach
    public void setUp() {
        baseUrl = "http://localhost:" + port + "/api/auth";
        userRepository.deleteAll();

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault());

        RestTemplateBuilder builder = new RestTemplateBuilder()
                .rootUri(baseUrl)
                .requestFactory(() -> requestFactory)
                .errorHandler(new DefaultResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse response) throws IOException {
                        HttpStatus statusCode = response.getStatusCode();
                        return statusCode.series() == HttpStatus.Series.SERVER_ERROR;
                    }
                });

        this.restTemplate = new TestRestTemplate(builder);
    }

    @Test
    public void testInvalidLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("nonexistentuser");
        loginRequest.setPassword("wrongpassword");

        ResponseEntity<ApiResponse<TokenResponse>> response = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid credentials.");
        assertThat(response.getBody().getData()).isNull();
    }


    @Test
    public void testUserRegistrationAndLogin() {
        // Register a new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("SecurePass123");

        ResponseEntity<ApiResponse<SuccessResponse>> registerResponse = restTemplate.exchange(
                "/register",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                new ParameterizedTypeReference<ApiResponse<SuccessResponse>>() {}
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().getMessage()).isEqualTo("User registered successfully.");
        assertThat(registerResponse.getBody().getData()).isNotNull();
        assertThat(registerResponse.getBody().getData().getMessage()).isEqualTo("User registered successfully.");

        // Attempt to login with correct credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("SecurePass123");

        ResponseEntity<ApiResponse<TokenResponse>> loginResponse = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getMessage()).isEqualTo("Login successful.");
        assertThat(loginResponse.getBody().getData()).isNotNull();
        assertThat(loginResponse.getBody().getData().getToken()).isNotEmpty();

        // Attempt to login with incorrect password
        loginRequest.setPassword("WrongPass");

        ResponseEntity<ApiResponse<TokenResponse>> failedLoginResponse = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(failedLoginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(failedLoginResponse.getBody()).isNotNull();
        assertThat(failedLoginResponse.getBody().getMessage()).isEqualTo("Invalid credentials.");
        assertThat(failedLoginResponse.getBody().getData()).isNull();
    }


    @Test
    public void testPasswordChangeAndTokenInvalidation() {
        // Register a new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("SecurePass123");

        ResponseEntity<ApiResponse<SuccessResponse>> registerResponse = restTemplate.exchange(
                "/register",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                new ParameterizedTypeReference<ApiResponse<SuccessResponse>>() {}
        );

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerResponse.getBody()).isNotNull();
        assertThat(registerResponse.getBody().getMessage()).isEqualTo("User registered successfully.");
        assertThat(registerResponse.getBody().getData()).isNotNull();
        assertThat(registerResponse.getBody().getData().getMessage()).isEqualTo("User registered successfully.");

        // Attempt to login with correct credentials
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("SecurePass123");

        ResponseEntity<ApiResponse<TokenResponse>> loginResponse = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).isNotNull();
        assertThat(loginResponse.getBody().getMessage()).isEqualTo("Login successful.");
        assertThat(loginResponse.getBody().getData()).isNotNull();
        assertThat(loginResponse.getBody().getData().getToken()).isNotEmpty();

        String oldToken = loginResponse.getBody().getData().getToken();

        // Verify token validity
        User user = userRepository.findByUsernameWithPasswordHistories("testuser").orElse(null);
        assertThat(user).isNotNull();
        boolean isOldTokenValid = jwtUtil.validateToken(oldToken, user);
        assertThat(isOldTokenValid).isTrue();

        // Change the user's password
        ResponseEntity<ApiResponse<SuccessResponse>> changePasswordResponse = restTemplate.exchange(
                "/change-password?username=testuser&newPassword=NewSecurePass456",
                HttpMethod.POST,
                new HttpEntity<>(null, new HttpHeaders()),
                new ParameterizedTypeReference<ApiResponse<SuccessResponse>>() {}
        );

        assertThat(changePasswordResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(changePasswordResponse.getBody()).isNotNull();
        assertThat(changePasswordResponse.getBody().getMessage()).isEqualTo("Password updated successfully.");
        assertThat(changePasswordResponse.getBody().getData()).isNotNull();
        assertThat(changePasswordResponse.getBody().getData().getMessage()).isEqualTo("Password updated successfully.");

        // Re-fetch the user to get the updated randomHash
        User updatedUser = userRepository.findByUsernameWithPasswordHistories("testuser").orElse(null);
        assertThat(updatedUser).isNotNull();

        // Attempt to login with old password
        LoginRequest loginRequestOld = new LoginRequest();
        loginRequestOld.setUsername("testuser");
        loginRequestOld.setPassword("SecurePass123");

        ResponseEntity<ApiResponse<TokenResponse>> oldLoginResponse = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestOld),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(oldLoginResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(oldLoginResponse.getBody()).isNotNull();
        assertThat(oldLoginResponse.getBody().getMessage()).isEqualTo("Invalid credentials.");
        assertThat(oldLoginResponse.getBody().getData()).isNull();

        // Attempt to login with new password
        LoginRequest loginRequestNew = new LoginRequest();
        loginRequestNew.setUsername("testuser");
        loginRequestNew.setPassword("NewSecurePass456");

        ResponseEntity<ApiResponse<TokenResponse>> newLoginResponse = restTemplate.exchange(
                "/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequestNew),
                new ParameterizedTypeReference<ApiResponse<TokenResponse>>() {}
        );

        assertThat(newLoginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(newLoginResponse.getBody()).isNotNull();
        assertThat(newLoginResponse.getBody().getMessage()).isEqualTo("Login successful.");
        assertThat(newLoginResponse.getBody().getData()).isNotNull();
        assertThat(newLoginResponse.getBody().getData().getToken()).isNotEmpty();

        String newToken = newLoginResponse.getBody().getData().getToken();

        boolean isOldTokenStillValid = jwtUtil.validateToken(oldToken, updatedUser);
        assertThat(isOldTokenStillValid).isFalse();

        boolean isNewTokenValid = jwtUtil.validateToken(newToken, updatedUser);
        assertThat(isNewTokenValid).isTrue();
    }

}
