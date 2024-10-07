package com.msinyu.jwtservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user in the system.
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username of the user.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Hashed password of the user.
     */
    @Column(nullable = false)
    private String passwordHash;

    /**
     * Unique random hash for the user to enhance token security.
     */
    @Column(nullable = false, unique = true, length = 64)
    private String randomHash;

    /**
     * One-to-Many relationship with PasswordHistory.
     * CascadeType.ALL ensures that all operations (persist, merge, remove, etc.) are cascaded.
     * orphanRemoval=true ensures that removing a PasswordHistory from the list deletes it from the database.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt DESC")
    private List<PasswordHistory> passwordHistories = new ArrayList<>();

    /**
     * Adds a new PasswordHistory record.
     *
     * @param history The PasswordHistory to add.
     */
    public void addPasswordHistory(PasswordHistory history) {
        passwordHistories.add(history);
        history.setUser(this);
    }

    /**
     * Removes a PasswordHistory record.
     *
     * @param history The PasswordHistory to remove.
     */
    public void removePasswordHistory(PasswordHistory history) {
        passwordHistories.remove(history);
        history.setUser(null);
    }
}
