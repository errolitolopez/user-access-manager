package com.example.demo.config.constants;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class to hold constant keys for application configurations.
 * This centralizes the configuration keys used by ApplicationConfigService,
 * making them easier to manage and reference consistently throughout the application.
 */
public final class ApplicationConfigKeys {

    /**
     * Configuration key for a comma-separated list of public URLs.
     */
    public static final String SECURITY_PUBLIC_URLS = "security.public.urls";

    // --- Security and Authentication ---
    /**
     * Configuration key for a comma-separated list of private URLs.
     */
    public static final String SECURITY_PRIVATE_URLS = "security.private.urls";
    /**
     * Configuration key for the JWT secret key.
     */
    public static final String JWT_SECRET_KEY = "application.security.jwt.secret-key";
    /**
     * Configuration key for the JWT expiration time in milliseconds.
     */
    public static final String JWT_EXPIRATION = "application.security.jwt.expiration";
    /**
     * Configuration key to enable or disable rate limiting.
     */
    public static final String RATE_LIMIT_ENABLED = "rate.limit.enabled";

    // --- Rate Limiting ---
    /**
     * Configuration key for a comma-separated list of rate-limited URLs.
     */
    public static final String RATE_LIMIT_INCLUDED_URLS = "rate.limit.included-urls";
    /**
     * Configuration key for a comma-separated list of URLs to be excluded from rate-limiting.
     */
    public static final String RATE_LIMIT_EXCLUDED_URLS = "rate.limit.excluded-urls";
    /**
     * Configuration key for the rate limit capacity.
     */
    public static final String RATE_LIMIT_CAPACITY = "rate.limit.capacity";
    /**
     * Configuration key for the rate limit refill duration in minutes.
     */
    public static final String RATE_LIMIT_REFILL_DURATION_MINUTES = "rate.limit.refill.duration-minutes";
    /**
     * Configuration key for the base URL used in password reset emails.
     */
    public static final String PASSWORD_RESET_BASE_URL = "password.reset.base.url";

    // --- Password Reset and User Management ---
    /**
     * Configuration key for the password reset token expiration time in minutes.
     */
    public static final String PASSWORD_RESET_TOKEN_EXPIRATION_MINUTES = "password.reset.token.expiration-minutes";
    /**
     * Configuration key for the type of password reset token to generate (e.g., "alphanumeric", "numeric").
     */
    public static final String PASSWORD_RESET_TOKEN_TYPE = "password.reset.token.type";
    /**
     * Configuration key for the maximum number of failed login attempts before an account is locked.
     */
    public static final String MAX_FAILED_LOGIN_ATTEMPTS = "max.failed.login.attempts";
    /**
     * Configuration key for the time in minutes after which a failed login attempt counter is reset.
     */
    public static final String LOCKOUT_RESET_MINUTES = "lockout.reset.minutes";
    /**
     * Configuration key for the number of years after which a user's account will expire.
     */
    public static final String ACCOUNT_EXPIRATION_YEARS = "account.expiration.years";
    /**
     * Configuration key for the time in minutes after which a locked account will be automatically unlocked.
     */
    public static final String ACCOUNT_UNLOCK_TIME_MINUTES = "account.unlock.time.minutes";
    /**
     * Configuration key for the number of days after which a user's password credentials will expire.
     */
    public static final String CREDENTIAL_EXPIRATION_DAYS = "credential.expiration.days";
    /**
     * Configuration key for the maximum size of audit log details in characters.
     */
    public static final String AUDIT_LOG_DETAILS_MAX_SIZE = "audit.log.details.max-size";

    // --- Audit Logging ---
    /**
     * Configuration key for the cooldown period in minutes between consecutive audit logs for the same actor.
     */
    public static final String AUDIT_LOG_COOLDOWN_MINUTES = "audit.log.cooldown-minutes";
    /**
     * Configuration key for the maximum number of audit log records to retain in the database.
     */
    public static final String AUDIT_LOG_MAX_SIZE = "audit.log.max-size";

    // Prevent instantiation of this utility class.
    private ApplicationConfigKeys() {
    }

    public static List<String> getAllKeys() {
        List<String> keys = new ArrayList<>();
        try {
            Class<?> clazz = ApplicationConfigKeys.class;
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isPublic(field.getModifiers()) &&
                        Modifier.isStatic(field.getModifiers()) &&
                        Modifier.isFinal(field.getModifiers()) &&
                        field.getType() == String.class) {
                    keys.add((String) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            System.err.println("Error accessing field via reflection: " + e.getMessage());
        }
        return keys;
    }

}
