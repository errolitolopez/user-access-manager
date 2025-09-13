package com.example.demo.user.dto;

import com.example.demo.util.RegexUtil;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) for updating a user's email address.
 */
public class UpdateUserEmailDto implements Serializable {

    /**
     * The unique identifier of the user whose email is to be updated.
     * Must not be null.
     */
    @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL)
    private Long id;

    /**
     * The new email address for the user.
     * Must conform to a standard email format.
     */
    @Pattern(regexp = RegexUtil.EMAIL_PATTERN, message = ValidationMessages.EMAIL_INVALID)
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
