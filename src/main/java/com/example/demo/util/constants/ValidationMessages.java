package com.example.demo.util.constants;

/**
 * A utility class to hold all constant strings for validation messages.
 * This centralizes messages, making them easier to manage and reuse.
 */
public final class ValidationMessages {


    // Prevent instantiation of this utility class
    private ValidationMessages() {
    }
    // General Messages
    public static final String ID_CANNOT_BE_NULL = "ID cannot be null.";
    public static final String NAME_ALREADY_EXIST = "Name is already in use.";
    public static final String NAME_CANNOT_BE_BLANK = "Name cannot be blank.";
    public static final String DATABASE_ERROR = "Database error: A resource with the provided details may already exist or violates a constraint.";
    public static final String ACCESS_DENIED = "Access Denied: You do not have permission to access this resource.";
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String VALIDATION_FAILED = "Input validation failed. Please check the detailed errors.";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected internal server error occurred. Please contact support.";
    public static final String INVALID_JWT_TOKEN = "Invalid or expired JWT token.";
    public static final String NEW_PASSWORD_SAME_AS_OLD = "New password cannot be the same as the old password.";

    // User Messages
    public static final String USERNAME_CANNOT_BE_BLANK = "Username cannot be blank.";
    public static final String USERNAME_INVALID = "Username must be 8-20 characters long and can only contain letters, numbers, underscores, and periods.";
    public static final String USERNAME_ALREADY_EXISTS = "Username is already taken.";
    public static final String EMAIL_CANNOT_BE_BLANK = "Email cannot be blank.";
    public static final String EMAIL_INVALID = "Email is not valid.";
    public static final String EMAIL_ALREADY_EXISTS = "Email is already in use.";
    public static final String PASSWORD_CANNOT_BE_BLANK = "Password cannot be blank.";
    public static final String PASSWORD_TOO_SHORT = "Password must be at least 8 characters long.";
    public static final String PASSWORD_INVALID = "Password must be 8-32 characters long. It must contain at least one uppercase letter, one lowercase letter, one number, and one special character.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String PASSWORD_MISMATCH = "Passwords do not match.";

}
