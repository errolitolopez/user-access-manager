package com.example.demo.config.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a dynamic application configuration setting stored in the database.
 */
@Entity
@Table(name = "application_configs")
public class ApplicationConfig {

    /**
     * The unique identifier for the configuration entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique key for the configuration setting.
     */
    @Column(name = "config_key", unique = true, nullable = false)
    private String configKey;

    /**
     * The value of the configuration setting.
     */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    /**
     * A user-friendly description of the configuration setting.
     */
    @Column(name = "config_description", columnDefinition = "TEXT")
    private String description;

    /**
     * A flag indicating if the configuration is active.
     */
    @Column(columnDefinition = "boolean default true")
    private Boolean enabled = true;

    /**
     * The username of the user who created this entry.
     */
    @Column(name = "created_by")
    private String createdBy;

    /**
     * The username of the user who last updated this entry.
     */
    @Column(name = "updated_by")
    private String updatedBy;

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

    public ApplicationConfig() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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
