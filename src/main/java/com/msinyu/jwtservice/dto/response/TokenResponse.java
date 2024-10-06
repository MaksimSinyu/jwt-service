package com.msinyu.jwtservice.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for JWT token responses.
 */
@Getter
@NoArgsConstructor
public class TokenResponse {

    /**
     * The JWT token string.
     */
    private String token;

    /**
     * Constructs a TokenResponse with the provided token.
     *
     * @param token JWT token string.
     */
    @JsonCreator
    public TokenResponse(@JsonProperty("token") String token) {
        this.token = token;
    }
}
