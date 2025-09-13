package com.example.demo.logging;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for masking sensitive data within strings.
 * This class provides a centralized and reusable way to apply masking rules.
 */
public final class MaskingUtils {

    // This regex captures the sensitive key part (group 1) and its value (group 2).
    // It avoids using lookbehind to be compatible with more regex engines and avoid warnings.
    // Group 1: The key, separator, and optional quotes (e.g., "password":")
    // Group 2: The sensitive value itself.
    private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile(
            "((?i)[\"']?(?:password|clientSecret|apiKey|ipAddress)[\"']?\\s*[:=]\\s*[\"']?)([^,'\"}]+)"
    );

    private static final String MASK = "********";

    private MaskingUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Masks known sensitive fields within a given string message.
     *
     * @param message The raw, unmasked log message.
     * @return The message with sensitive data replaced by asterisks.
     */
    public static String maskSensitiveData(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }

        // Use a Matcher to find all occurrences of the pattern.
        Matcher matcher = SENSITIVE_DATA_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();

        // Loop through all matches and rebuild the string with the value masked.
        // This approach is safer than in-place modification of a StringBuilder.
        while (matcher.find()) {
            // Replaces the entire matched string (group 0) with the prefix (group 1) followed by the mask.
            matcher.appendReplacement(sb, Matcher.quoteReplacement(matcher.group(1) + MASK));
        }
        // Append the rest of the string that did not match.
        matcher.appendTail(sb);

        return sb.toString();
    }
}

