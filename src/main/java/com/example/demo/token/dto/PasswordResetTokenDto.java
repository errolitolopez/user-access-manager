package com.example.demo.token.dto;

import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.user.dto.UserDto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for the PasswordResetToken entity.
 * Provides a secure way to transfer password reset token data.
 */
public class PasswordResetTokenDto {

    /**
     * The unique identifier for the password reset token.
     */
    private Long id;

    /**
     * The unique, non-guessable token string.
     */
    private String token;

    /**
     * The user associated with this token.
     */
    private UserDto user;

    /**
     * The date and time when the token expires.
     */
    private LocalDateTime expiryDate;

    /**
     * The current status of the token.
     */
    private PasswordResetTokenStatus status;

    public PasswordResetTokenDto() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public PasswordResetTokenStatus getStatus() {
        return status;
    }

    public void setStatus(PasswordResetTokenStatus status) {
        this.status = status;
    }
}
