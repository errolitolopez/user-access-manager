package com.example.demo.config.service.impl;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.dto.ApplicationConfigDto;
import com.example.demo.config.entity.ApplicationConfig;
import com.example.demo.config.mapper.ApplicationConfigMapper;
import com.example.demo.config.repository.ApplicationConfigRepository;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.service.IdentityService;
import com.example.demo.util.enums.EventType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service implementation for managing dynamic application configurations.
 */
@Service
public class ApplicationConfigServiceImpl implements ApplicationConfigService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigServiceImpl.class);
    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    private final ApplicationConfigRepository applicationConfigRepository;
    private final ApplicationConfigMapper applicationConfigMapper;
    private final IdentityService identityService;
    private final ApplicationEventPublisher eventPublisher;

    public ApplicationConfigServiceImpl(ApplicationConfigRepository applicationConfigRepository,
                                        ApplicationConfigMapper applicationConfigMapper,
                                        IdentityService identityService,
                                        ApplicationEventPublisher eventPublisher) {
        this.applicationConfigRepository = applicationConfigRepository;
        this.applicationConfigMapper = applicationConfigMapper;
        this.identityService = identityService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Initializes the in-memory cache with all enabled configurations from the database
     * when the application starts.
     */
    @PostConstruct
    public void init() {
        refreshCache();
    }

    // --- Core CRUD Operations ---

    @Override
    @Transactional
    public ApplicationConfigDto create(ApplicationConfigDto applicationConfigDto) {
        applicationConfigRepository.findByConfigKey(applicationConfigDto.getConfigKey())
                .ifPresent(config -> {
                    throw new InvalidInputException("Configuration with key '" + applicationConfigDto.getConfigKey() + "' already exists.");
                });

        ApplicationConfig applicationConfig = applicationConfigMapper.toEntity(applicationConfigDto);
        applicationConfig.setId(null);
        applicationConfig.setCreatedBy(identityService.getAuthenticatedUsername());
        applicationConfig.setUpdatedBy(identityService.getAuthenticatedUsername());
        ApplicationConfig savedConfig = applicationConfigRepository.save(applicationConfig);

        // Add the new configuration to the cache if enabled
        if (Boolean.TRUE.equals(savedConfig.getEnabled())) {
            configCache.put(savedConfig.getConfigKey(), savedConfig.getConfigValue());
        }

        // Publish the event instead of direct call
        Map<String, Object> details = new HashMap<>();
        details.put("configKey", savedConfig.getConfigKey());
        details.put("configValue", savedConfig.getConfigValue());
        eventPublisher.publishEvent(new AuditLogEvent(this, identityService.getAuthenticatedUsername(), null, EventType.CREATE_CONFIG, details));

        return applicationConfigMapper.toDto(savedConfig);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ApplicationConfig configToDelete = applicationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration not found with ID: " + id));

        // Remove the configuration from the cache
        configCache.remove(configToDelete.getConfigKey());
        applicationConfigRepository.delete(configToDelete);

        // Publish the event instead of direct call
        Map<String, Object> details = new HashMap<>();
        details.put("configKey", configToDelete.getConfigKey());
        eventPublisher.publishEvent(new AuditLogEvent(this, identityService.getAuthenticatedUsername(), null, EventType.DELETE_CONFIG, details));
    }

    @Override
    @Transactional
    public ApplicationConfigDto update(ApplicationConfigDto applicationConfigDto) {
        ApplicationConfig existingConfig = applicationConfigRepository.findById(applicationConfigDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Configuration not found with ID: " + applicationConfigDto.getId()));

        if (applicationConfigDto.getConfigKey() != null && !existingConfig.getConfigKey().equals(applicationConfigDto.getConfigKey())) {
            // Check if the new key already exists for another configuration
            applicationConfigRepository.findByConfigKey(applicationConfigDto.getConfigKey())
                    .ifPresent(config -> {
                        throw new InvalidInputException("Configuration with key '" + applicationConfigDto.getConfigKey() + "' already exists.");
                    });
            // Update the cache with the new key
            configCache.remove(existingConfig.getConfigKey());
            if (Boolean.TRUE.equals(existingConfig.getEnabled())) {
                configCache.put(applicationConfigDto.getConfigKey(), applicationConfigDto.getConfigValue());
            }
        } else if (applicationConfigDto.getConfigValue() != null && !existingConfig.getConfigValue().equals(applicationConfigDto.getConfigValue())) {
            // Update the cache if the value changes
            if (Boolean.TRUE.equals(existingConfig.getEnabled())) {
                configCache.put(existingConfig.getConfigKey(), applicationConfigDto.getConfigValue());
            }
        }

        existingConfig.setConfigKey(applicationConfigDto.getConfigKey());
        existingConfig.setConfigValue(applicationConfigDto.getConfigValue());
        existingConfig.setUpdatedBy(identityService.getAuthenticatedUsername());
        ApplicationConfig updatedConfig = applicationConfigRepository.save(existingConfig);

        // Publish the event instead of direct call
        Map<String, Object> details = new HashMap<>();
        details.put("configKey", updatedConfig.getConfigKey());
        details.put("configValue", updatedConfig.getConfigValue());
        eventPublisher.publishEvent(new AuditLogEvent(this, identityService.getAuthenticatedUsername(), null, EventType.UPDATE_CONFIG, details));

        return applicationConfigMapper.toDto(updatedConfig);
    }

    @Override
    @Transactional
    public void updateEnabledStatus(Long id, Boolean enabled) {
        ApplicationConfig configToUpdate = applicationConfigRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration not found with ID: " + id));

        configToUpdate.setEnabled(enabled);
        configToUpdate.setUpdatedBy(identityService.getAuthenticatedUsername());
        applicationConfigRepository.save(configToUpdate);

        // Update the cache based on the new status
        if (enabled) {
            configCache.put(configToUpdate.getConfigKey(), configToUpdate.getConfigValue());
        } else {
            configCache.remove(configToUpdate.getConfigKey());
        }

        // Publish the event instead of direct call
        Map<String, Object> details = new HashMap<>();
        details.put("configKey", configToUpdate.getConfigKey());
        details.put("enabled", enabled);
        eventPublisher.publishEvent(new AuditLogEvent(this, identityService.getAuthenticatedUsername(), null, EventType.TOGGLE_CONFIG_ENABLED, details));
    }

    // --- Read Operations ---

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationConfigDto> getAll(ApplicationConfigDto applicationConfigDto, Pageable pageable) {
        ApplicationConfig applicationConfigExample = applicationConfigMapper.toEntity(applicationConfigDto);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<ApplicationConfig> example = Example.of(applicationConfigExample, matcher);

        return applicationConfigRepository.findAll(example, pageable)
                .map(applicationConfigMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationConfigDto getById(Long id) {
        return applicationConfigRepository.findById(id)
                .map(applicationConfigMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Configuration not found with ID: " + id));
    }

    @Override
    public String getValue(String key) {
        return configCache.get(key);
    }

    @Override
    @Transactional(readOnly = true)
    public void refreshCache() {
        logger.info("Refreshing application configuration cache...");
        ConcurrentHashMap<String, String> tempCache = new ConcurrentHashMap<>();
        loadAllEnabledConfigsToCache(tempCache);

        List<String> definedKeys = ApplicationConfigKeys.getAllKeys();
        Set<String> loadedKeys = tempCache.keySet();

        List<String> missingKeys = definedKeys.stream()
                .filter(key -> !loadedKeys.contains(key))
                .collect(Collectors.toList());

        if (!missingKeys.isEmpty()) {
            logger.warn("The following application configuration keys are missing from the database: {}", missingKeys);
        }

        configCache.clear();
        configCache.putAll(tempCache);
        logger.info("Application configuration cache refreshed. Loaded {} entries.", configCache.size());
    }

    @Override
    @Transactional(readOnly = true)
    public void loadAllEnabledConfigsToCache(ConcurrentHashMap<String, String> cache) {
        applicationConfigRepository.findAllByEnabled(true)
                .forEach(config -> cache.put(config.getConfigKey(), config.getConfigValue()));
    }
}
