package com.example.demo.security.dto;

import com.example.demo.util.RegexUtil;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) for completing a password reset.
 * This DTO is used to capture the new password and the reset token.
 */
public class PasswordResetDto {

    /**
     * The unique token sent to the user's email.
     * Must not be blank.
     */
    @NotBlank
    private String token;

    /**
     * The new password for the user.
     * Must conform to the application's strong password policy.
     */
    @Pattern(regexp = RegexUtil.STRONG_PASSWORD_PATTERN, message = ValidationMessages.PASSWORD_INVALID)
    private String newPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
