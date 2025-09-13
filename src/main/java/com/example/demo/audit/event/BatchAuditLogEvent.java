package com.example.demo.audit.event;

import com.example.demo.util.enums.EventType;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Map;

/**
 * A custom event fired when a batch of audit logs needs to be created.
 * This is used for operations like scheduled tasks where multiple records
 * are processed and logged at once.
 */
public class BatchAuditLogEvent extends ApplicationEvent {

    private final String username;
    private final String ipAddress;
    private final EventType eventType;
    private final List<Map<String, Object>> detailsList;

    public BatchAuditLogEvent(Object source, String username, String ipAddress, EventType eventType, List<Map<String, Object>> detailsList) {
        super(source);
        this.username = username;
        this.ipAddress = ipAddress;
        this.eventType = eventType;
        this.detailsList = detailsList;
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

    public List<Map<String, Object>> getDetailsList() {
        return detailsList;
    }
}
