package com.example.demo.token.repository;

import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.token.enums.PasswordResetTokenStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository for the PasswordResetToken entity.
 * Provides standard CRUD operations and custom query methods for password reset tokens.
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Finds a password reset token by its unique token string.
     *
     * @param token The token string to search for.
     * @return An Optional containing the found token, or empty if not found.
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Finds a pending password reset token for a specific user that is not yet processed and has not expired.
     *
     * @param user The user entity associated with the token.
     * @param now The current date and time to check against the token's expiry date.
     * @return An Optional containing the found token, or empty if no valid pending token exists.
     */
    Optional<PasswordResetToken> findByUserAndExpiryDateAfterAndStatus(User user, LocalDateTime now, PasswordResetTokenStatus status);

    /**
     * Finds a token for a user that has not expired and has a status within the specified collection.
     *
     * @param user The user entity associated with the token.
     * @param now The current date and time to check against the token's expiry date.
     * @param statuses The collection of statuses to search for.
     * @return A list containing the found tokens, or an empty list if no valid token exists.
     */
    List<PasswordResetToken> findByUserAndExpiryDateAfterAndStatusIn(User user, LocalDateTime now, Collection<PasswordResetTokenStatus> statuses);

    /**
     * Finds all password reset tokens that are either expired or have been used.
     *
     * @param now The current date and time to check against the token's expiry date.
     * @param status The status to check for used tokens.
     * @return A list of tokens that are ready for cleanup.
     */
    List<PasswordResetToken> findByExpiryDateBeforeAndStatus(LocalDateTime now, PasswordResetTokenStatus status);

    /**
     * Finds all password reset tokens that are expired .
     *
     * @param now The current date and time to check against the token's expiry date.
     * @return A list of tokens that are ready for cleanup.
     */
    List<PasswordResetToken> findByExpiryDateBefore(LocalDateTime now);

    /**
     * Finds all password reset tokens by their status.
     *
     * @param status The status of the tokens to find.
     * @return A list of PasswordResetToken entities matching the given status.
     */
    List<PasswordResetToken> findAllByStatus(PasswordResetTokenStatus status);
}
