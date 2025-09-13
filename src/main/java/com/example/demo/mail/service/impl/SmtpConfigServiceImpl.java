package com.example.demo.mail.service.impl;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mail.dto.CreateSmtpConfigDto;
import com.example.demo.mail.dto.SmtpConfigDto;
import com.example.demo.mail.entity.SmtpConfig;
import com.example.demo.mail.mapper.SmtpConfigMapper;
import com.example.demo.mail.repository.SmtpConfigRepository;
import com.example.demo.mail.service.SmtpConfigService;
import com.example.demo.security.service.CryptoService;
import com.example.demo.security.service.IdentityService;
import com.example.demo.util.enums.EventType;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for managing SMTP configurations.
 */
@Service
public class SmtpConfigServiceImpl implements SmtpConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SmtpConfigServiceImpl.class);

    private final SmtpConfigRepository smtpConfigRepository;
    private final SmtpConfigMapper smtpConfigMapper;

    private final IdentityService identityService;
    private final CryptoService cryptoService;
    private final ApplicationEventPublisher eventPublisher;

    public SmtpConfigServiceImpl(SmtpConfigRepository smtpConfigRepository, SmtpConfigMapper smtpConfigMapper, IdentityService identityService, ApplicationEventPublisher eventPublisher, CryptoService cryptoService) {
        this.smtpConfigRepository = smtpConfigRepository;
        this.smtpConfigMapper = smtpConfigMapper;
        this.identityService = identityService;
        this.eventPublisher = eventPublisher;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional
    public SmtpConfigDto create(CreateSmtpConfigDto createSmtpConfigDto) {
        smtpConfigRepository.findByName(createSmtpConfigDto.getName()).ifPresent(config -> {
            throw new InvalidInputException("SMTP configuration with name '" + createSmtpConfigDto.getName() + "' already exists.");
        });

        // Use the new DTO for mapping
        SmtpConfig smtpConfig = smtpConfigMapper.toEntity(createSmtpConfigDto);
        smtpConfig.setPassword(cryptoService.encrypt(createSmtpConfigDto.getPassword()));
        SmtpConfig savedConfig = smtpConfigRepository.save(smtpConfig);

        logSmtpConfigEvent(EventType.CREATE_SMTP_CONFIGS, savedConfig.getName());

        return smtpConfigMapper.toDto(savedConfig);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SmtpConfig configToDelete = smtpConfigRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SMTP configuration not found with ID: " + id));

        smtpConfigRepository.delete(configToDelete);

        logSmtpConfigEvent(EventType.DELETE_SMTP_CONFIGS, configToDelete.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SmtpConfigDto> getAll(SmtpConfigDto smtpConfigDto, Pageable pageable) {
        SmtpConfig smtpConfigExample = smtpConfigMapper.toEntity(smtpConfigDto);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<SmtpConfig> example = Example.of(smtpConfigExample, matcher);

        return smtpConfigRepository.findAll(example, pageable).map(smtpConfig -> {
            SmtpConfigDto dto = smtpConfigMapper.toDto(smtpConfig);
            dto.setPassword(cryptoService.decrypt(dto.getPassword()));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<SmtpConfigDto> getAllEnabledConfigs() {
        return smtpConfigRepository.findAllByEnabled(true).stream().map(smtpConfig -> {
            SmtpConfigDto dto = smtpConfigMapper.toDto(smtpConfig);
            dto.setPassword(cryptoService.decrypt(dto.getPassword()));
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SmtpConfigDto getById(Long id) {
        return smtpConfigRepository.findById(id).map(smtpConfig -> {
            SmtpConfigDto dto = smtpConfigMapper.toDto(smtpConfig);
            dto.setPassword(cryptoService.decrypt(dto.getPassword()));
            return dto;
        }).orElseThrow(() -> new ResourceNotFoundException("SMTP configuration not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public SmtpConfigDto getByName(String name) {
        return smtpConfigRepository.findByName(name).map(smtpConfig -> {
            SmtpConfigDto dto = smtpConfigMapper.toDto(smtpConfig);
            dto.setPassword(cryptoService.decrypt(dto.getPassword()));
            return dto;
        }).orElseThrow(() -> new ResourceNotFoundException("SMTP configuration not found with name: " + name));
    }

    @Override
    @Transactional
    public void incrementSentCount(String configName) {
        Optional<SmtpConfig> configOptional = smtpConfigRepository.findByName(configName);

        if (configOptional.isEmpty()) {
            logger.warn("Attempted to increment sent count for non-existent SMTP configuration: {}", configName);
            return;
        }

        SmtpConfig config = configOptional.get();
        config.setCurrentSentCount(config.getCurrentSentCount() + 1);
        smtpConfigRepository.save(config);
    }

    @Override
    @Transactional
    public SmtpConfigDto update(SmtpConfigDto smtpConfigDto) {
        SmtpConfig existingConfig = smtpConfigRepository.findById(smtpConfigDto.getId()).orElseThrow(() -> new ResourceNotFoundException("SMTP configuration not found with ID: " + smtpConfigDto.getId()));

        if (!existingConfig.getName().equals(smtpConfigDto.getName())) {
            smtpConfigRepository.findByName(smtpConfigDto.getName()).ifPresent(config -> {
                if (!config.getId().equals(smtpConfigDto.getId())) {
                    throw new InvalidInputException("SMTP configuration with name '" + smtpConfigDto.getName() + "' already exists.");
                }
            });
        }

        SmtpConfig updatedConfig = smtpConfigMapper.toEntity(smtpConfigDto);
        updatedConfig.setPassword(cryptoService.encrypt(smtpConfigDto.getPassword()));
        updatedConfig.setDateCreated(existingConfig.getDateCreated());
        SmtpConfig savedConfig = smtpConfigRepository.save(updatedConfig);

        logSmtpConfigEvent(EventType.UPDATE_SMTP_CONFIGS, savedConfig.getName());

        return smtpConfigMapper.toDto(savedConfig);
    }

    @Override
    @Transactional
    public void updateEnabledStatus(Long id, Boolean enabled) {
        SmtpConfig configToUpdate = smtpConfigRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SMTP configuration not found with ID: " + id));

        configToUpdate.setEnabled(enabled);
        smtpConfigRepository.save(configToUpdate);

        String username = identityService.getAuthenticatedUsername();
        if (username == null) {
            username = "System";
        }
        Map<String, Object> details = new HashMap<>();
        details.put("smtpConfigName", configToUpdate.getName());
        details.put("status", enabled);
        eventPublisher.publishEvent(new AuditLogEvent(this, username, null, EventType.UPDATE_SMTP_CONFIGS, details));
    }

    /**
     * Helper method to publish an SMTP configuration-related audit event.
     *
     * @param eventType  The type of event to log.
     * @param configName The name of the SMTP configuration.
     */
    private void logSmtpConfigEvent(EventType eventType, String configName) {
        Map<String, Object> details = new HashMap<>();
        details.put("smtpConfigName", configName);
        eventPublisher.publishEvent(new AuditLogEvent(this, identityService.getAuthenticatedUsername(), null, eventType, details));
    }
}
