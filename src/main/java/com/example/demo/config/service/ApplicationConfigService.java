package com.example.demo.config.service;

import com.example.demo.config.dto.ApplicationConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing dynamic application configurations.
 */
public interface ApplicationConfigService {

    // --- Core CRUD Operations ---
    /**
     * Creates a new configuration entry.
     *
     * @param applicationConfigDto The configuration data.
     * @return The created DTO.
     */
    ApplicationConfigDto create(ApplicationConfigDto applicationConfigDto);

    /**
     * Deletes a configuration entry by ID.
     *
     * @param id The ID of the configuration to delete.
     */
    void delete(Long id);

    /**
     * Updates an existing configuration entry.
     *
     * @param applicationConfigDto The DTO with updated data.
     * @return The updated DTO.
     */
    ApplicationConfigDto update(ApplicationConfigDto applicationConfigDto);

    /**
     * Toggles the enabled status of a configuration entry.
     *
     * @param id The ID of the configuration to update.
     * @param enabled The new enabled status.
     */
    void updateEnabledStatus(Long id, Boolean enabled);

    // --- Read Operations ---
    /**
     * Retrieves a paginated list of configurations based on search criteria.
     *
     * @param applicationConfigDto The DTO for search filtering.
     * @param pageable             Pagination and sorting information.
     * @return A page of ApplicationConfigDtos.
     */
    Page<ApplicationConfigDto> getAll(ApplicationConfigDto applicationConfigDto, Pageable pageable);

    /**
     * Retrieves a configuration entry by its unique ID.
     *
     * @param id The ID of the configuration.
     * @return The found DTO.
     */
    ApplicationConfigDto getById(Long id);

    /**
     * Retrieves a configuration value from the in-memory cache.
     *
     * @param key The key of the configuration to retrieve.
     * @return The configuration value as a String, or null if not found.
     */
    String getValue(String key);

    /**
     * Refreshes the in-memory cache with all enabled configurations from the database.
     */
    void refreshCache();

    /**
     * Loads all enabled configurations from the database into a cache.
     *
     * @param configCache A thread-safe map to store the configurations.
     */
    void loadAllEnabledConfigsToCache(ConcurrentHashMap<String, String> configCache);
}
