package com.example.demo.audit.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for the AuditLog entity.
 * <p>
 * This DTO is used to transfer audit log data, providing a clean separation
 * between the internal database representation and the external API contract.
 * </p>
 */
public class AuditLogDto {

    /**
     * The unique identifier for the audit log entry.
     */
    private Long id;

    /**
     * The timestamp of the event.
     */
    private LocalDateTime dateCreated;

    /**
     * The username of the user who performed the action.
     * This field can be null if the event was system-initiated or unauthenticated.
     */
    private String username;

    /**
     * The IP address from which the action was initiated.
     * This field can be null if the event was system-initiated.
     */
    private String ipAddress;

    /**
     * The type of the event, e.g., "CREATE_USER", "UPDATE_PASSWORD".
     */
    private String eventType;

    /**
     * A JSON string containing additional details about the event.
     */
    private String details;

    public AuditLogDto() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
