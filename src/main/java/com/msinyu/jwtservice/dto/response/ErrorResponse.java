package com.msinyu.jwtservice.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * DTO for error responses.
 */
public class ErrorResponse {
    @Getter
    private final String message;

    @JsonCreator
    public ErrorResponse(@JsonProperty("message") String message) {
        this.message = message;
    }
}