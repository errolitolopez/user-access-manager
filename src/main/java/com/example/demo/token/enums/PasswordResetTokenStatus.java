package com.example.demo.token.enums;

/**
 * Defines the possible states for a password reset token.
 * This enum provides a more expressive and type-safe alternative to a simple boolean flag.
 */
public enum PasswordResetTokenStatus {
    /**
     * The token has been created and is waiting to be processed by the email scheduler.
     */
    PENDING,
    /**
     * An email containing the token has been successfully sent to the user.
     */
    SENT,
    /**
     * The token has expired and is no longer valid.
     */
    EXPIRED
}
