package com.example.demo.config.dto;

import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Data Transfer Object for the ApplicationConfig entity.
 * This DTO is used to transfer configuration data and includes validation constraints.
 */
public class ApplicationConfigDto implements Serializable {

    /**
     * The unique identifier for the configuration entry.
     */
    private Long id;

    /**
     * The unique key for the configuration setting.
     */
    @NotBlank(message = "Configuration key " + ValidationMessages.NAME_CANNOT_BE_BLANK)
    private String configKey;

    /**
     * The value of the configuration setting.
     */
    @NotBlank(message = "Configuration value " + ValidationMessages.NAME_CANNOT_BE_BLANK)
    private String configValue;

    /**
     * A user-friendly description of the configuration setting.
     */
    private String description;

    /**
     * A flag indicating if the configuration is active.
     */
    private Boolean enabled;

    public ApplicationConfigDto() {
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
}
