--
-- File: schema.sql
-- Description: This script defines the complete schema for a basic security and access control system.
--              It includes tables for users, roles, permissions, audit logs, and dynamic configurations.
--
-- Rationale:
-- - Separates tables into logical groups (Core, Join, Utility) for clarity.
-- - Uses foreign keys to establish relationships between tables.
-- - Includes timestamps and flags for comprehensive user and system auditing.
--

--
-- Section: Drop Existing Tables
-- Purpose: Ensures the script can be re-executed without conflicts by removing tables
--          in the correct dependency order.
--
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS password_reset_tokens;
DROP TABLE IF EXISTS application_configs;
DROP TABLE IF EXISTS smtp_configs;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS permissions;

--
-- Section: Core Tables
--

-- Table: `permissions`
-- Stores a list of all possible actions or permissions in the system.
CREATE TABLE permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    date_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) AUTO_INCREMENT = 1000000;

-- Table: `roles`
-- Stores a list of all roles that can be assigned to users.
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    date_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) AUTO_INCREMENT = 1000000;

-- Table: `users`
-- Stores user account information, with fields logically grouped.
CREATE TABLE users (
    -- Identification and Authentication
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,

    -- Account State and Status
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    account_expired BOOLEAN NOT NULL DEFAULT FALSE,
    account_expiration_date DATETIME(6) NOT NULL,
    credentials_expired BOOLEAN NOT NULL DEFAULT FALSE,

    -- Password Management
    password_last_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),

    -- Login Failure Tracking
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_failed_login_time DATETIME(6) DEFAULT NULL,

    -- Auditing and Metadata
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    date_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) AUTO_INCREMENT = 1000000;

--
-- Section: Join Tables
--

-- Table: `user_roles`
-- A many-to-many join table to link users to one or more roles.
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id, role_id),
    -- Foreign key to the users table
    FOREIGN KEY (user_id) REFERENCES users(id),
    -- Foreign key to the roles table
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Table: `role_permissions`
-- A many-to-many join table to link roles to one or more permissions.
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (role_id, permission_id),
    -- Foreign key to the roles table
    FOREIGN KEY (role_id) REFERENCES roles(id),
    -- Foreign key to the permissions table
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

--
-- Section: Utility and Configuration Tables
--

-- Table: `audit_logs`
-- Stores a log of all significant user-related actions for auditing purposes.
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    username VARCHAR(255),
    ip_address VARCHAR(255),
    event_type VARCHAR(255) NOT NULL,
    details TEXT
) AUTO_INCREMENT = 1000000;

-- Table: `password_reset_tokens`
-- Stores temporary tokens for the password reset functionality.
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date DATETIME(6) NOT NULL,
    status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (user_id) REFERENCES users(id)
) AUTO_INCREMENT = 1000000;

-- Table: `application_configs`
-- Stores key-value pairs for dynamic application configuration.
CREATE TABLE application_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    config_description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    date_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) AUTO_INCREMENT = 1000000;

-- Table: `smtp_configs`
-- Stores multiple dynamic SMTP configuration settings, each with its own sending limits.
CREATE TABLE smtp_configs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    smtp_auth BOOLEAN NOT NULL,
    starttls_enabled BOOLEAN NOT NULL,
    limit_size INT NOT NULL,
    current_sent_count INT NOT NULL DEFAULT 0,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    date_created DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    date_updated DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
) AUTO_INCREMENT = 1000000;
