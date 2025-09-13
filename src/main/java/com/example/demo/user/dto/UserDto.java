package com.example.demo.user.dto;

import com.example.demo.role.dto.RoleDto;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for the User entity.
 */
public class UserDto {

    /**
     * The unique identifier for the user.
     */
    private Long id;

    /**
     * The unique username of the user.
     */
    private String username;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * A flag indicating if the user's account is enabled.
     */
    private Boolean enabled;

    /**
     * A flag indicating if the user's account is locked.
     */
    private Boolean accountLocked;

    /**
     * A flag indicating if the user's account has expired.
     */
    private Boolean accountExpired;

    /**
     * The date and time when the user's account will expire.
     */
    private LocalDateTime accountExpirationDate;

    /**
     * A flag indicating if the user's credentials have expired.
     */
    private Boolean credentialsExpired;

    /**
     * The date and time when the user's password was last updated.
     */
    private LocalDateTime passwordLastUpdated;

    /**
     * The number of failed login attempts.
     */
    private Integer failedLoginAttempts;

    /**
     * The date and time of the last failed login attempt.
     */
    private LocalDateTime lastFailedLoginTime;

    /**
     * The date and time when the user was created.
     */
    private LocalDateTime dateCreated;

    /**
     * The date and time when the user was last updated.
     */
    private LocalDateTime dateUpdated;

    /**
     * The set of roles assigned to the user.
     */
    private Set<RoleDto> roles;

    public UserDto() {
    }

    public UserDto(Long id, String username, String email, Boolean enabled, Boolean accountLocked, Boolean accountExpired, LocalDateTime accountExpirationDate, Boolean credentialsExpired, LocalDateTime passwordLastUpdated, Integer failedLoginAttempts, LocalDateTime lastFailedLoginTime, LocalDateTime dateCreated, LocalDateTime dateUpdated, Set<RoleDto> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
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

    public Set<RoleDto> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDto> roles) {
        this.roles = roles;
    }
}
