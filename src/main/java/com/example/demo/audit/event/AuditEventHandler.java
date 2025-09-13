package com.example.demo.audit.event;

import com.example.demo.audit.service.AuditService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditEventHandler {

    private final AuditService auditService;

    public AuditEventHandler(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    @Async
    public void handleAuditLogEvent(AuditLogEvent event) {
        this.auditService.logEvent(event.getUsername(), event.getIpAddress(), event.getEventType(), event.getDetails());
    }

    @EventListener
    @Async
    public void handleBatchAuditLogEvent(BatchAuditLogEvent event) {
        this.auditService.logEvents(event.getUsername(), event.getIpAddress(), event.getEventType(), event.getDetailsList());
    }
}
