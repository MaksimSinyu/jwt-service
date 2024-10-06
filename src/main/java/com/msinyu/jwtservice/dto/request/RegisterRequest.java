package com.msinyu.jwtservice.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for user registration requests.
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

}
