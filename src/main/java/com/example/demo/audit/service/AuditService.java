package com.example.demo.audit.service;

import com.example.demo.audit.dto.AuditLogDto;
import com.example.demo.audit.dto.QueryAuditLogDto;
import com.example.demo.util.enums.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for managing the application's audit trail.
 */
public interface AuditService {

    /**
     * Retrieves a paginated list of audit logs based on search criteria.
     *
     * @param queryAuditLogDto DTO containing fields for filtering the search.
     * @param pageable    Pagination and sorting information.
     * @return A page of audit log DTOs.
     */
    Page<AuditLogDto> getAll(QueryAuditLogDto queryAuditLogDto, Pageable pageable);

    /**
     * Logs an audit event asynchronously.
     * <p>
     * This method is designed to be non-blocking, allowing the calling thread to
     * continue its work without waiting for the audit log to be persisted.
     *
     * @param username The username of the user who performed the action.
     * @param ipAddress     The IP address of the user who performed the action.
     * @param eventType     The type of the event (e.g., CREATE_USER).
     * @param details       A map of details to be serialized into a JSON string.
     * @return A CompletableFuture that completes when the event has been logged.
     */
    CompletableFuture<Void> logEvent(String username, String ipAddress, EventType eventType, Map<String, Object> details);

    /**
     * Logs multiple audit events in a single batch asynchronously.
     * <p>
     * This method is optimized for scenarios where multiple events are generated
     * in a single operation, such as scheduled tasks.
     *
     * @param username The username of the user who performed the actions.
     * @param ipAddress     The IP address of the user who performed the actions.
     * @param eventType     The type of the event (e.g., ACCOUNT_EXPIRED).
     * @param detailsList   A list of maps, where each map contains details for a specific event.
     * @return A CompletableFuture that completes when all events have been logged.
     */
    CompletableFuture<Void> logEvents(String username, String ipAddress, EventType eventType, List<Map<String, Object>> detailsList);
}
