package com.example.demo.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utility class for validating URL paths.
 * Provides a method to check if a given string is a valid URL path.
 */
public final class UrlUtil {

    /**
     * Regex pattern to validate a URL path.
     * A valid path starts with a forward slash and can contain letters, numbers,
     * forward slashes, hyphens, and underscores. It can optionally end with '/**'.
     */
    private static final Pattern URL_PATH_PATTERN = Pattern.compile("^/[a-zA-Z0-9-/_]+(?:/\\*\\*)*$");

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UrlUtil() {
    }

    /**
     * Checks if a given string is a valid URL path.
     *
     * @param urlPath The string to be validated.
     * @return {@code true} if the string is a valid URL path, {@code false} otherwise.
     */
    public static boolean isValidUrlPath(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) {
            return false;
        }
        return URL_PATH_PATTERN.matcher(urlPath).matches();
    }

    /**
     * Checks if a given string is a proper, well-formed URL.
     *
     * @param urlString The string to be validated.
     * @return {@code true} if the string is a valid URL, {@code false} otherwise.
     */
    public static boolean isValidUrl(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
