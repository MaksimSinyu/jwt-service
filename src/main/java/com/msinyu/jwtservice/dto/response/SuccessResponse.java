package com.msinyu.jwtservice.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO for success responses.
 */
@Getter
@NoArgsConstructor
public class SuccessResponse {

    /**
     * Success message.
     */
    private String message;

    /**
     * Constructs a SuccessResponse with the provided message.
     *
     * @param message Success message.
     */
    @JsonCreator
    public SuccessResponse(@JsonProperty("message") String message) {
        this.message = message;
    }
}
