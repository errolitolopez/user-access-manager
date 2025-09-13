package com.example.demo.util.enums;

/**
 * Defines a set of standardized event types for the application's audit trail.
 * This enum ensures consistency and prevents typos when logging events.
 */
public enum EventType {
    // User Management Events
    CREATE_USER,
    UPDATE_EMAIL,
    UPDATE_PASSWORD,
    DELETE_USER,

    // Role and Permission Events
    ASSIGN_ROLE,
    REMOVE_ROLE,
    CREATE_ROLE,
    ASSIGN_PERMISSION_TO_ROLE,
    REMOVE_PERMISSION_FROM_ROLE,

    // Account Status Events
    WAIVE_STATUS,
    WAIVE_ACCOUNT_EXPIRATION,
    AUTHENTICATION_SUCCESS,
    AUTHENTICATION_FAILURE,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    CREDENTIALS_EXPIRED,
    AUDIT_LOG_CLEANUP,
    ACCESS_DENIED,
    TOO_MANY_REQUESTS,
    INVALID_INPUT,
    RESOURCE_NOT_FOUND,
    ACCOUNT_EXPIRED,

    // Configuration Management Events
    CREATE_CONFIG,
    UPDATE_CONFIG,
    DELETE_CONFIG,
    PASSWORD_RESET_EMAIL_SENT,
    PASSWORD_RESET,
    TOGGLE_CONFIG_ENABLED,
    CONFIG_MISSING,

    // Mail Service Events
    CREATE_SMTP_CONFIGS,
    READ_SMTP_CONFIGS,
    UPDATE_SMTP_CONFIGS,
    DELETE_SMTP_CONFIGS,
    SMTP_CONFIG_UNAVAILABLE,

    // Token Management Events
    CREATE_PASSWORD_RESET_TOKEN,
    PASSWORD_RESET_TOKEN_CLEANUP
}
