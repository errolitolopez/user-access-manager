package com.example.demo.security.dto;

import com.example.demo.util.RegexUtil;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object (DTO) for requesting a password reset.
 * This DTO is used to capture the user's email address.
 */
public class PasswordResetRequestDto {

    /**
     * The email address of the user requesting a password reset.
     * Must be a valid email format.
     */
    @NotBlank(message = ValidationMessages.EMAIL_CANNOT_BE_BLANK)
    @Pattern(regexp = RegexUtil.EMAIL_PATTERN, message = ValidationMessages.EMAIL_INVALID)
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
