package com.msinyu.jwtservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Entry point for the JWT Service Application.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.msinyu.jwtservice.repository")
public class JwtServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtServiceApplication.class, args);
    }
}
