package com.example.demo.mail.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.io.Serializable;

/**
 * Data Transfer Object for creating a new SmtpConfig.
 * This DTO contains only the fields required for the creation process,
 * separating the input model from the full entity and read models.
 */
public class CreateSmtpConfigDto implements Serializable {

    /**
     * The unique name for the SMTP configuration.
     */
    @NotBlank(message = "Name cannot be blank.")
    private String name;

    /**
     * The hostname of the SMTP server.
     */
    @NotBlank(message = "Host cannot be blank.")
    private String host;

    /**
     * The port number of the SMTP server.
     */
    @NotNull(message = "Port cannot be null.")
    @Min(value = 1, message = "Port must be a positive number.")
    private Integer port;

    /**
     * The username for authenticating with the SMTP server.
     */
    @NotBlank(message = "Username cannot be blank.")
    private String username;

    /**
     * The password for authenticating with the SMTP server.
     */
    @NotBlank(message = "Password cannot be blank.")
    private String password;

    /**
     * A flag indicating if SMTP authentication is required.
     */
    @NotNull(message = "SMTP authentication flag cannot be null.")
    private Boolean smtpAuth;

    /**
     * A flag indicating if STARTTLS encryption is enabled.
     */
    @NotNull(message = "STARTTLS enabled flag cannot be null.")
    private Boolean starttlsEnabled;

    /**
     * The maximum number of emails that can be sent using this configuration.
     * A value of 0 indicates no limit.
     */
    @NotNull(message = "Limit size cannot be null.")
    @Min(value = 0, message = "Limit size must be a non-negative number.")
    private Integer limitSize;

    public CreateSmtpConfigDto() {
    }

    public CreateSmtpConfigDto(String name, String host, Integer port, String username, String password, Boolean smtpAuth, Boolean starttlsEnabled, Integer limitSize) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.smtpAuth = smtpAuth;
        this.starttlsEnabled = starttlsEnabled;
        this.limitSize = limitSize;
    }

    // Getters and Setters
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
}
