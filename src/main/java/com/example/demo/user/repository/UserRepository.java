package com.example.demo.user.repository;

import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * @param username The username of the user to find.
     * @return An Optional containing the found user, or empty if no user is found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email.
     * @param email The email of the user to find.
     * @return An Optional containing the found user, or empty if no user is found.
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a list of users by their username or email.
     * @param username The username to search for.
     * @param email The email to search for.
     * @return A list of User entities that match the given username or email.
     */
    List<User> findByUsernameOrEmail(String username, String email);

    /**
     * Finds all users whose credentials have expired and the credentials expired flag is false.
     * @param date The date to compare against.
     * @return A list of users with expired credentials.
     */
    List<User> findAllByPasswordLastUpdatedBeforeAndCredentialsExpiredIsFalse(LocalDateTime date);

    /**
     * Finds all users whose account is locked.
     * @param unlockTime The date before which locked accounts can be unlocked.
     * @return A list of users who can be unlocked.
     */
    List<User> findAllByLastFailedLoginTimeBeforeAndAccountLockedIsTrue(LocalDateTime unlockTime);

    /**
     * Finds all users whose account expiration date is before the given date and are not already expired.
     * @param date The date to compare against.
     * @return A list of users whose accounts are to be expired.
     */
    List<User> findAllByAccountExpirationDateBeforeAndAccountExpiredIsFalse(LocalDateTime date);
}
