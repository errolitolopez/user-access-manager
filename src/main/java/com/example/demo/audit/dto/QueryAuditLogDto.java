package com.example.demo.audit.dto;

import com.example.demo.util.enums.EventType;
import java.io.Serializable;

/**
 * Data Transfer Object for querying audit logs.
 * This DTO contains only the fields relevant for filtering and searching
 * audit log records, providing a clean separation from the full {@link AuditLogDto}.
 */
public class QueryAuditLogDto implements Serializable {

    /**
     * The username of the actor who performed the action.
     */
    private String username;

    /**
     * The type of the event, e.g., "CREATE_USER", "UPDATE_PASSWORD".
     */
    private EventType eventType;

    public QueryAuditLogDto() {
    }

    public QueryAuditLogDto(String username, EventType eventType) {
        this.username = username;
        this.eventType = eventType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
}
