package com.example.demo.audit.service.impl;

import com.example.demo.audit.dto.AuditLogDto;
import com.example.demo.audit.dto.QueryAuditLogDto;
import com.example.demo.audit.entity.AuditLog;
import com.example.demo.audit.mapper.AuditLogMapper;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.audit.service.AuditService;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.util.enums.EventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for managing the application's audit trail.
 * This service is designed to be called asynchronously to avoid blocking the main thread.
 */
@Service
public class AuditServiceImpl implements AuditService {
    private static final Logger logger = LoggerFactory.getLogger(AuditServiceImpl.class);
    private static final int DETAILS_MAX_SIZE_DEFAULT = 2000;
    private static final int AUDIT_LOG_COOLDOWN_MINUTES_DEFAULT = 5;
    private final ConcurrentHashMap<String, LocalDateTime> lastLoggedAuditTime = new ConcurrentHashMap<>();

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final ApplicationConfigService applicationConfigService;
    private final ObjectMapper objectMapper;

    public AuditServiceImpl(AuditLogRepository auditLogRepository,
                            AuditLogMapper auditLogMapper,
                            ApplicationConfigService applicationConfigService,
                            ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.auditLogMapper = auditLogMapper;
        this.applicationConfigService = applicationConfigService;
        this.objectMapper = objectMapper;
    }


    /**
     * Retrieves a paginated list of audit logs based on search criteria.
     *
     * @param queryAuditLogDto DTO containing fields for filtering the search.
     * @param pageable    Pagination and sorting information.
     * @return A page of audit log DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDto> getAll(QueryAuditLogDto queryAuditLogDto, Pageable pageable) {
        AuditLog auditLogExample = new AuditLog();
        if (queryAuditLogDto.getUsername() != null) {
            auditLogExample.setUsername(queryAuditLogDto.getUsername());
        }
        if (queryAuditLogDto.getEventType() != null) {
            auditLogExample.setEventType(queryAuditLogDto.getEventType().name());
        }

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<AuditLog> example = Example.of(auditLogExample, matcher);

        return auditLogRepository.findAll(example, pageable)
                .map(auditLogMapper::toDto);
    }

    /**
     * Logs an audit event asynchronously.
     *
     * @param username  The username of the user performing the action.
     * @param ipAddress The IP address of the user.
     * @param eventType The type of event (e.g., "CREATE_USER").
     * @param details   A map of details to be serialized into a JSON string.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public CompletableFuture<Void> logEvent(String username, String ipAddress, EventType eventType, Map<String, Object> details) {
        String detailsJson = serializeAndTrimDetails(eventType, details);
        AuditLog auditLog = new AuditLog(username, ipAddress, eventType.name(), detailsJson);
        auditLogRepository.save(auditLog);
        logger.info("Audit log saved successfully for eventType: {}, username: {}, ipAddress: {}, details: {}", eventType, username, ipAddress, detailsJson);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Logs multiple audit events in a single batch asynchronously.
     *
     * @param username    The username of the user who performed the actions.
     * @param ipAddress   The IP address of the user.
     * @param eventType   The type of the event (e.g., ACCOUNT_EXPIRED).
     * @param detailsList A list of maps, where each map contains details for a specific event.
     * @return A CompletableFuture that completes when all events have been logged.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public CompletableFuture<Void> logEvents(String username, String ipAddress, EventType eventType, List<Map<String, Object>> detailsList) {
        List<AuditLog> auditLogs = detailsList.stream()
                .map(details -> new AuditLog(username, ipAddress, eventType.name(), serializeAndTrimDetails(eventType, details)))
                .collect(Collectors.toList());
        auditLogRepository.saveAll(auditLogs);
        logger.info("Audit logs saved successfully for {} events of type: {}", auditLogs.size(), eventType);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Serializes a map of details to a JSON string and trims it if it exceeds the max size.
     *
     * @param eventType The type of event for logging purposes.
     * @param details   The map of details to serialize.
     * @return The serialized and potentially trimmed JSON string, or null on error.
     */
    private String serializeAndTrimDetails(EventType eventType, Map<String, Object> details) {
        try {
            int detailsMaxSize = getDetailsMaxSize();
            String detailsJson = objectMapper.writeValueAsString(details);
            if (detailsJson.length() > detailsMaxSize) {
                logger.warn("Audit log details for event type {} exceeded max size. Trimming from {} to {}.", eventType, detailsJson.length(), detailsMaxSize);
                return detailsJson.substring(0, detailsMaxSize);
            }
            return detailsJson;
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize audit log details for event type {}: {}", eventType, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Retrieves the configured maximum size for audit log details, with a fallback to a default value.
     *
     * @return The configured or default maximum size.
     */
    private int getDetailsMaxSize() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.AUDIT_LOG_DETAILS_MAX_SIZE))
                .map(Integer::parseInt)
                .orElse(DETAILS_MAX_SIZE_DEFAULT);
    }

    /**
     * Retrieves the configured cooldown period for audit logging, with a fallback to a default value.
     *
     * @return The configured or default cooldown period in minutes.
     */
    private int getCooldownMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.AUDIT_LOG_COOLDOWN_MINUTES))
                .map(Integer::parseInt)
                .orElse(AUDIT_LOG_COOLDOWN_MINUTES_DEFAULT);
    }
}
