package com.example.demo.audit.event;

import com.example.demo.util.enums.EventType;
import org.springframework.context.ApplicationEvent;
import java.util.Map;

/**
 * Custom event fired when a new audit log entry should be created.
 * This decouples the service that triggers the event from the audit logging service.
 */
public class AuditLogEvent extends ApplicationEvent {
    private final String username;
    private final String ipAddress;
    private final EventType eventType;
    private final Map<String, Object> details;

    public AuditLogEvent(Object source, String username, String ipAddress, EventType eventType, Map<String, Object> details) {
        super(source);
        this.username = username;
        this.ipAddress = ipAddress;
        this.eventType = eventType;
        this.details = details;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public EventType getEventType() {
        return eventType;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
