package com.example.demo.mail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Data Transfer Object for the SmtpConfig entity.
 * This DTO is used to transfer SMTP configuration data and includes validation constraints.
 */
public class SmtpConfigDto implements Serializable {

    /**
     * The unique identifier for the SMTP configuration.
     */
    private Long id;

    /**
     * The unique name for the SMTP configuration.
     */
    @NotBlank
    private String name;

    /**
     * The hostname of the SMTP server.
     */
    @NotBlank
    private String host;

    /**
     * The port number of the SMTP server.
     */
    private Integer port;

    /**
     * The username for authenticating with the SMTP server.
     */
    @NotBlank
    private String username;

    /**
     * The password for authenticating with the SMTP server.
     */
    @NotBlank
    private String password;

    /**
     * A flag indicating if SMTP authentication is required.
     */
    @NotNull
    private Boolean smtpAuth;

    /**
     * A flag indicating if STARTTLS encryption is enabled.
     */
    @NotNull
    private Boolean starttlsEnabled;

    /**
     * The maximum number of emails that can be sent using this configuration.
     */
    @NotNull
    private Integer limitSize;

    /**
     * The current number of emails sent using this configuration.
     */
    private Integer currentSentCount;

    /**
     * A flag indicating if this SMTP configuration is currently active.
     */
    @NotNull
    private Boolean enabled;

    public SmtpConfigDto() {
    }

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
}