package com.example.demo.token.service;

import com.example.demo.security.dto.PasswordResetDto;
import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.user.entity.User;

/**
 * Service interface for managing password reset tokens.
 */
public interface PasswordResetTokenService {

    /**
     * Creates a new password reset token for a given user.
     * @param user The user for whom the token is being created.
     * @return The created PasswordResetToken entity.
     */
    PasswordResetToken createToken(User user);

    /**
     * Resets the user's password using a valid token and new password.
     * @param passwordResetDto DTO containing the token and new password.
     */
    void resetPassword(PasswordResetDto passwordResetDto);
}
