package com.msinyu.jwtservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents the history of a user's passwords.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "password_history")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The hashed password.
     */
    @Column(nullable = false, length = 255)
    private String passwordHash;

    /**
     * The password vector derived from the hashed password.
     */
    @Lob
    @Column(name = "password_vector", nullable = false)
    private String passwordVector;

    /**
     * Timestamp when the password was set.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Many-to-One relationship with User.
     * optional=false ensures that a PasswordHistory must be associated with a User.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

}
