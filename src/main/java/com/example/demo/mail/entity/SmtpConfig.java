package com.example.demo.mail.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a dynamic SMTP configuration setting stored in the database.
 * Each entry holds the details for a specific email server,
 * including connection parameters and email sending limits.
 */
@Entity
@Table(name = "smtp_configs")
public class SmtpConfig {

    /**
     * The unique identifier for the SMTP configuration entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique name for the SMTP configuration (e.g., "Default Gmail", "Marketing Server").
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * The hostname of the SMTP server.
     */
    @Column(nullable = false)
    private String host;

    /**
     * The port number of the SMTP server.
     */
    @Column(nullable = false)
    private Integer port;

    /**
     * The username for authenticating with the SMTP server.
     */
    @Column(nullable = false)
    private String username;

    /**
     * The password for authenticating with the SMTP server.
     */
    @Column(nullable = false)
    private String password;

    /**
     * A flag indicating if SMTP authentication is required.
     */
    @Column(name = "smtp_auth", nullable = false)
    private Boolean smtpAuth;

    /**
     * A flag indicating if STARTTLS encryption is enabled.
     */
    @Column(name = "starttls_enabled", nullable = false)
    private Boolean starttlsEnabled;

    /**
     * The maximum number of emails that can be sent using this configuration.
     * A value of 0 or less indicates no limit.
     */
    @Column(name = "limit_size")
    private Integer limitSize;

    /**
     * The current number of emails sent using this configuration since the counter was last reset.
     */
    @Column(name = "current_sent_count")
    private Integer currentSentCount = 0;

    /**
     * A flag indicating if this SMTP configuration is currently active and can be used to send emails.
     */
    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = true;

    /**
     * The date and time when the configuration was created.
     * This field is automatically populated on creation.
     */
    @CreationTimestamp
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    /**
     * The date and time when the configuration was last updated.
     * This field is automatically populated on each update.
     */
    @UpdateTimestamp
    @Column(name = "date_updated", nullable = false)
    private LocalDateTime dateUpdated;

    public SmtpConfig() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getSmtpAuth() {
        return smtpAuth;
    }

    public void setSmtpAuth(Boolean smtpAuth) {
        this.smtpAuth = smtpAuth;
    }

    public Boolean getStarttlsEnabled() {
        return starttlsEnabled;
    }

    public void setStarttlsEnabled(Boolean starttlsEnabled) {
        this.starttlsEnabled = starttlsEnabled;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public Integer getCurrentSentCount() {
        return currentSentCount;
    }

    public void setCurrentSentCount(Integer currentSentCount) {
        this.currentSentCount = currentSentCount;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
}
