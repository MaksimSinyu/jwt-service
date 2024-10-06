package com.msinyu.jwtservice.repository;

import com.msinyu.jwtservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username, fetching password histories eagerly.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found.
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.passwordHistories WHERE u.username = :username")
    Optional<User> findByUsernameWithPasswordHistories(@Param("username") String username);
}
