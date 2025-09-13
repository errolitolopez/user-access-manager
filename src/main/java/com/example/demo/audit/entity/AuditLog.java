package com.example.demo.audit.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents an audit log entry, tracking significant user-related actions.
 */
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The timestamp of the event. Automatically set on creation.
     */
    @CreationTimestamp
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    /**
     * The username of the actor who performed the action.
     */
    @Column(name = "username")
    private String username;

    /**
     * The IP address of the actor who performed the action.
     */
    @Column(name = "ip_address")
    private String ipAddress;

    /**
     * The type of the event, e.g., "CREATE_USER", "UPDATE_PASSWORD".
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;

    /**
     * A JSON string containing additional, specific details about the event.
     */
    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public AuditLog() {
    }

    public AuditLog(String username, String ipAddress, String eventType, String details) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.eventType = eventType;
        this.details = details;
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
