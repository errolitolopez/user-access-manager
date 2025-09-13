package com.example.demo.user.entity;

import com.example.demo.role.entity.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Represents a User entity in the database.
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique username of the user.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * The email address of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The hashed password of the user.
     */
    @Column(nullable = false)
    private String password;

    /**
     * A flag indicating if the user's account is enabled.
     */
    @Column(columnDefinition = "boolean default false")
    private Boolean enabled = false;

    /**
     * A flag indicating if the user's account is locked.
     */
    @Column(columnDefinition = "boolean default false")
    private Boolean accountLocked = false;

    /**
     * A flag indicating if the user's account has expired.
     */
    @Column(columnDefinition = "boolean default false")
    private Boolean accountExpired = false;

    /**
     * The date and time when the user's account will expire.
     */
    @Column(name = "account_expiration_date")
    private LocalDateTime accountExpirationDate;

    /**
     * A flag indicating if the user's credentials have expired.
     */
    @Column(columnDefinition = "boolean default false")
    private Boolean credentialsExpired = false;

    /**
     * The date and time when the user's password was last updated.
     */
    @Column(name = "password_last_updated")
    private LocalDateTime passwordLastUpdated;

    /**
     * The number of failed login attempts.
     */
    @Column(name = "failed_login_attempts", nullable = false)
    private Integer failedLoginAttempts = 0;

    /**
     * The date and time of the last failed login attempt.
     */
    @Column(name = "last_failed_login_time")
    private LocalDateTime lastFailedLoginTime;

    /**
     * The date and time when the user was created.
     */
    @CreationTimestamp
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    /**
     * The date and time when the user was last updated.
     */
    @UpdateTimestamp
    @Column(name = "date_updated", nullable = false)
    private LocalDateTime dateUpdated;

    /**
     * The set of roles assigned to the user.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public User() {
    }

    public User(Long id, String username, String email, String password, Boolean enabled, Boolean accountLocked, Boolean accountExpired, LocalDateTime accountExpirationDate, Boolean credentialsExpired, LocalDateTime passwordLastUpdated, Integer failedLoginAttempts, LocalDateTime lastFailedLoginTime, LocalDateTime dateCreated, LocalDateTime dateUpdated, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.accountLocked = accountLocked;
        this.accountExpired = accountExpired;
        this.accountExpirationDate = accountExpirationDate;
        this.credentialsExpired = credentialsExpired;
        this.passwordLastUpdated = passwordLastUpdated;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lastFailedLoginTime = lastFailedLoginTime;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
        this.roles = roles;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Boolean getAccountExpired() {
        return accountExpired;
    }

    public void setAccountExpired(Boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public LocalDateTime getAccountExpirationDate() {
        return accountExpirationDate;
    }

    public void setAccountExpirationDate(LocalDateTime accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }

    public Boolean getCredentialsExpired() {
        return credentialsExpired;
    }

    public void setCredentialsExpired(Boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public LocalDateTime getPasswordLastUpdated() {
        return passwordLastUpdated;
    }

    public void setPasswordLastUpdated(LocalDateTime passwordLastUpdated) {
        this.passwordLastUpdated = passwordLastUpdated;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(LocalDateTime lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
