package com.example.demo.util;

import java.security.SecureRandom;

/**
 * Utility class for generating secure, numeric tokens for password resets.
 * This class ensures a consistent and centralized approach to creating tokens.
 */
public final class TokenGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    private TokenGenerator() {
        // Private constructor to prevent instantiation of this utility class.
    }

    /**
     * Generates a unique 6-digit numeric token for a password reset request.
     * The token is a random number between 100,000 and 999,999, inclusive.
     *
     * @return A 6-digit numeric token as a string.
     */
    public static String generateNumeric6DigitToken() {
        // Generate a random number between 100,000 (inclusive) and 1,000,000 (exclusive)
        int randomNumber = SECURE_RANDOM.nextInt(900000) + 100000;
        return String.valueOf(randomNumber);
    }

    /**
     * Generates a unique alphanumeric token of a specified length.
     *
     * @return An alphanumeric token as a string.
     */
    public static String generateAlphanumericToken(int length) {
        StringBuilder token = new StringBuilder(length);
        boolean lastCharWasLetter = SECURE_RANDOM.nextBoolean();

        for (int i = 0; i < length; i++) {
            boolean currentCharIsLetter = lastCharWasLetter;

            // If the last two characters were of the same type, force the next one to be different.
            if (i >= 2) {
                boolean secondLastCharWasLetter = Character.isLetter(token.charAt(i - 2));
                if (lastCharWasLetter && secondLastCharWasLetter) {
                    currentCharIsLetter = false;
                } else if (!lastCharWasLetter && !secondLastCharWasLetter) {
                    currentCharIsLetter = true;
                }
            }

            if (currentCharIsLetter) {
                token.append(LETTERS.charAt(SECURE_RANDOM.nextInt(LETTERS.length())));
            } else {
                token.append(DIGITS.charAt(SECURE_RANDOM.nextInt(DIGITS.length())));
            }
            lastCharWasLetter = currentCharIsLetter;
        }
        return token.toString();
    }

    /**
     * Generates a unique 6-character alphanumeric token for a password reset request.
     * The token contains a mix of letters and digits, ensuring that there are no
     * three consecutive characters of the same type.
     *
     * @return A 6-character alphanumeric token as a string.
     */
    public static String generateAlphanumericToken() {
        return generateAlphanumericToken(6);
    }
}
