package com.example.demo.user.dto;

import com.example.demo.util.RegexUtil;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Data Transfer Object for updating a user's password.
 */
public class UpdateUserPasswordDto {

    /**
     * The unique identifier of the user whose password is to be updated.
     * Must not be null.
     */
    @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL)
    private Long id;

    /**
     * The new password for the user.
     * Must conform to a standard password format.
     */
    @Pattern(regexp = RegexUtil.STRONG_PASSWORD_PATTERN, message = ValidationMessages.PASSWORD_INVALID)
    private String password;

    public UpdateUserPasswordDto() {
    }

    public UpdateUserPasswordDto(Long id, String password) {
        this.id = id;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
