package com.example.demo.user.dto;

import com.example.demo.util.RegexUtil;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for creating a new user.
 */
public class CreateUserDto implements Serializable {

    /**
     * The desired username for the new user.
     * Must conform to the specified pattern.
     */
    @Pattern(regexp = RegexUtil.USERNAME_PATTERN, message = ValidationMessages.USERNAME_INVALID)
    private String username;

    /**
     * The new password for the user.
     * Must conform to the specified pattern.
     */
    @Pattern(regexp = RegexUtil.STRONG_PASSWORD_PATTERN, message = ValidationMessages.PASSWORD_INVALID)
    private String password;

    /**
     * The email address for the new user.
     * Must conform to a standard email format.
     */
    @Pattern(regexp = RegexUtil.EMAIL_PATTERN, message = ValidationMessages.EMAIL_INVALID)
    private String email;

    public CreateUserDto() {
    }

    public CreateUserDto(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
