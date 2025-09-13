package com.example.demo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for common regular expression operations.
 * This class provides methods to validate various string formats using pre-compiled patterns for efficiency.
 */
public final class RegexUtil {

    /**
     * Pre-compiled pattern for email validation.
     */
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+(?:[_.-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+(?:[_.-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,3}$";
    private static final Pattern EMAIL_REGEX = Pattern.compile(EMAIL_PATTERN);

    /**
     * Pre-compiled pattern for username validation.
     */
    public static final String USERNAME_PATTERN = "(?!.*[.\\-_]{2,})^[a-zA-Z0-9.\\-_]{5,20}$";
    private static final Pattern USERNAME_REGEX = Pattern.compile(USERNAME_PATTERN);

    /**
     * Pre-compiled pattern for strong password validation.
     */
    public static final String STRONG_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,32})";
    private static final Pattern STRONG_PASSWORD_REGEX = Pattern.compile(STRONG_PASSWORD_PATTERN);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private RegexUtil() {
    }

    /**
     * Checks if a given string matches a specific regular expression pattern.
     * This method is an internal utility and is not exposed as a public API.
     *
     * @param pattern The compiled regular expression pattern.
     * @param input The string to validate.
     * @return {@code true} if the input matches the pattern, {@code false} otherwise.
     */
    private static boolean isMatch(Pattern pattern, String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * Validates if an email address is in a valid format.
     *
     * @param email The email string to validate.
     * @return {@code true} if the email is valid, {@code false} otherwise.
     */
    public static boolean isValidEmail(String email) {
        return isMatch(EMAIL_REGEX, email);
    }

    /**
     * Validates if a username meets the required format.
     *
     * @param username The username string to validate.
     * @return {@code true} if the username is valid, {@code false} otherwise.
     */
    public static boolean isValidUsername(String username) {
        return isMatch(USERNAME_REGEX, username);
    }

    /**
     * Validates if a password meets the strong password requirements.
     *
     * @param password The password string to validate.
     * @return {@code true} if the password is a strong password, {@code false} otherwise.
     */
    public static boolean isStrongPassword(String password) {
        return isMatch(STRONG_PASSWORD_REGEX, password);
    }
}
