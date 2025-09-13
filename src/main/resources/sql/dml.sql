--
-- File: dml.sql
-- Description: This script populates the database with initial sample data for a
--              basic security and access control system. It provides example entries
--              for users, roles, permissions, and their respective relationships.
--
-- Rationale:
-- - Provides a comprehensive set of permissions and roles (e.g., ADMIN, USER).
-- - Inserts diverse user accounts to test various security states (enabled, disabled, locked, expired).
-- - Establishes the foundational relationships between roles, permissions, and users.
--

--
-- Section: Insert Core Data
--

-- Insert permissions for various system actions.
INSERT INTO permissions (id, name) VALUES
    (1000000, 'CREATE_USERS'),
    (1000001, 'READ_USERS'),
    (1000002, 'UPDATE_USERS'),
    (1000003, 'DELETE_USERS'),
    (1000004, 'CREATE_ROLES'),
    (1000005, 'READ_ROLES'),
    (1000006, 'UPDATE_ROLES'),
    (1000007, 'DELETE_ROLES'),
    (1000008, 'CREATE_PERMISSIONS'),
    (1000009, 'READ_PERMISSIONS'),
    (1000010, 'UPDATE_PERMISSIONS'),
    (1000011, 'DELETE_PERMISSIONS'),
    (1000012, 'READ_AUDIT_LOGS'),
    (1000013, 'READ_CONFIG'),
    (1000014, 'CREATE_CONFIG'),
    (1000015, 'UPDATE_CONFIG'),
    (1000016, 'DELETE_CONFIG'),
    (1000017, 'CREATE_SMTP_CONFIGS'),
    (1000018, 'READ_SMTP_CONFIGS'),
    (1000019, 'UPDATE_SMTP_CONFIGS'),
    (1000020, 'DELETE_SMTP_CONFIGS');

-- Insert core roles for the application.
INSERT INTO roles (id, name) VALUES
    (1000000, 'ROLE_ADMIN'),
    (1000001, 'ROLE_USER');

--
-- Section: Insert User Accounts
--
-- The password hash '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u'
-- corresponds to the plain text password 'password'.
--

-- Insert various user accounts with all fields explicitly populated.
INSERT INTO users (id, username, password, email, enabled, account_locked, account_expired, account_expiration_date, credentials_expired, password_last_updated, failed_login_attempts, last_failed_login_time, date_created, date_updated) VALUES
    (1000000, 'admin', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'admin@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1000001, 'user1', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'user1@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1000002, 'user2', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'user2@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1000003, 'user3', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'user3@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    (1000004, 'locked_user', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'locked@example.com', TRUE, TRUE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 5, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 15 MINUTE), CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()), -- Account is locked.
    (1000005, 'disabled_user', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'disabled@example.com', FALSE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()), -- Account is disabled.
    (1000006, 'account_exp_user', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'accountexp@example.com', TRUE, FALSE, TRUE, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 1 DAY), FALSE, CURRENT_TIMESTAMP(), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()), -- Account has expired.
    (1000007, 'expired_user_1', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'expired1@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 91 DAY), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()), -- Password older than 90 days.
    (1000008, 'expired_user_2', '$2a$10$VWlaO.Q/y4Gvkal.gFO0U.EBB5AJrxaBFevj5EyX5hBpcsxlMnA1u', 'expired2@example.com', TRUE, FALSE, FALSE, DATE_ADD(CURRENT_TIMESTAMP(), INTERVAL 3 YEAR), FALSE, DATE_SUB(CURRENT_TIMESTAMP(), INTERVAL 120 DAY), 0, NULL, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()); -- Password older than 90 days.

--
-- Section: Establish Relationships
--

-- Link roles to permissions.
-- ROLE_ADMIN is granted all permissions.
INSERT INTO role_permissions (role_id, permission_id) VALUES
    (1000000, 1000000), (1000000, 1000001), (1000000, 1000002), (1000000, 1000003), (1000000, 1000004), (1000000, 1000005),
    (1000000, 1000006), (1000000, 1000007), (1000000, 1000008), (1000000, 1000009), (1000000, 1000010), (1000000, 1000011), (1000000, 1000012),
    (1000000, 1000013), (1000000, 1000014), (1000000, 1000015), (1000000, 1000016), (1000000, 1000017), (1000000, 1000018), (1000000, 1000019), (1000000, 1000020);

-- ROLE_USER is granted a basic set of read permissions.
INSERT INTO role_permissions (role_id, permission_id) VALUES
    (1000001, 1000001),  -- READ_USERS
    (1000001, 1000005),  -- READ_ROLES
    (1000001, 1000009); -- READ_PERMISSIONS

-- Link users to roles.
-- These IDs now correspond to the order of insertion in the users table.
INSERT INTO user_roles (user_id, role_id) VALUES
    (1000000, 1000000), -- 'admin' is assigned 'ROLE_ADMIN'.
    (1000001, 1000001), -- 'user1' is assigned 'ROLE_USER'.
    (1000002, 1000001), -- 'user2' is assigned 'ROLE_USER'.
    (1000003, 1000001); -- 'user3' is assigned 'ROLE_USER'.

--
-- Section: Initial Application Configurations
--
-- Insert a few example configuration settings to be managed dynamically.
-- These values can be updated at runtime via the new API endpoints.
--
INSERT INTO application_configs (config_key, config_value, config_description, created_by, updated_by, enabled) VALUES
    ('account.expiration.years', '3', 'The number of years after which a user''s account will expire.', 'System', 'System', TRUE),
    ('account.unlock.time.minutes', '30', 'The number of minutes after which a locked account will be automatically unlocked.', 'System', 'System', TRUE),
    ('application.security.jwt.expiration', '86400000', 'The expiration time for JWT tokens in milliseconds. (24 hours)', 'System', 'System', TRUE),
    ('application.security.jwt.secret-key', '404E635266556A586E32723545625F69666E38677271396F24422D6E31593475', 'The secret key used to sign and verify JWT tokens.', 'System', 'System', TRUE),
    ('audit.log.cooldown-minutes', '5', 'The cooldown period in minutes between logging consecutive audit events for the same actor.', 'System', 'System', TRUE),
    ('audit.log.details.max-size', '999999', 'The maximum size of the audit log details JSON string before it is trimmed.', 'System', 'System', TRUE),
    ('audit.log.max-size', '10000', 'The maximum number of audit log records to retain in the database.', 'System', 'System', TRUE),
    ('credential.expiration.days', '90', 'The number of days after which a user''s password credentials will expire.', 'System', 'System', TRUE),
    ('lockout.reset.minutes', '3', 'The time in minutes after which a failed login attempt counter is reset.', 'System', 'System', TRUE),
    ('max.failed.login.attempts', '10', 'The number of failed login attempts before a user account is locked.', 'System', 'System', TRUE),
    ('password.reset.base.url', 'http://localhost:8080/password-reset', 'The base URL for the password reset link sent to users.', 'System', 'System', TRUE),
    ('password.reset.token.expiration-minutes', '15', 'The expiration time for password reset tokens in minutes.', 'System', 'System', TRUE),
    ('rate.limit.capacity', '500', 'The number of requests allowed per user within the refill duration.', 'System', 'System', TRUE),
    ('rate.limit.enabled', 'true', 'A boolean flag to enable or disable the rate limiting functionality.', 'System', 'System', TRUE),
    ('rate.limit.excluded-urls', '/get,/search', 'A comma-separated list of URL suffixes to be excluded from rate-limiting.', 'System', 'System', TRUE),
    ('rate.limit.included-urls', '/api/auth/authenticate,/api/public/password-reset', 'A comma-separated list of URL prefixes to be rate-limited.', 'System', 'System', TRUE),
    ('rate.limit.refill.duration-minutes', '1', 'The duration in minutes for refilling the rate limit token bucket.', 'System', 'System', TRUE),
    ('security.private.urls', '/api/**', 'A comma-separated list of URL patterns that require authentication.', 'System', 'System', TRUE),
    ('security.public.urls', '/api/auth/**,/api/public/password-reset/**', 'A comma-separated list of URL patterns that are publicly accessible without authentication.', 'System', 'System', TRUE);

--
-- Section: Initial SMTP Configurations
--
-- Insert a few example SMTP configurations for email services.
-- These configurations can be managed at runtime.
--
INSERT INTO smtp_configs (id, name, host, port, username, password, smtp_auth, starttls_enabled, limit_size, current_sent_count, enabled, date_created, date_updated) VALUES
    (1000000, 'default_gmail', 'smtp.gmail.com', 587, 'username@gmail.com', 'password', TRUE, TRUE, 500, 0, TRUE, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
