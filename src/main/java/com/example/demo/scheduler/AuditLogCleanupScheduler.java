package com.example.demo.scheduler;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.audit.repository.AuditLogRepository;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.util.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A scheduled task to automatically clean up old audit log records.
 * This helps in managing database size and maintaining performance.
 */
@Component
public class AuditLogCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogCleanupScheduler.class);
    private static final int DEFAULT_MAX_SIZE = 999_999;

    private final AuditLogRepository auditLogRepository;
    private final ApplicationConfigService applicationConfigService;
    private final ApplicationEventPublisher eventPublisher;

    public AuditLogCleanupScheduler(AuditLogRepository auditLogRepository, ApplicationConfigService applicationConfigService, ApplicationEventPublisher eventPublisher) {
        this.auditLogRepository = auditLogRepository;
        this.applicationConfigService = applicationConfigService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * This scheduled job runs daily to delete the oldest audit log records.
     */
    @Scheduled(cron = "${scheduler.audit.cleanup.cron:0 0 0 */7 * ?}")
    @Transactional
    public void cleanupOldLogs() {
        int maxSize = getMaxSize();
        logger.info("Starting scheduled job: Cleaning up old audit logs with a max size of {}.", maxSize);

        long totalLogs = auditLogRepository.count();
        long recordsToDeleteCount = totalLogs - maxSize;

        if (recordsToDeleteCount > 0) {
            performCleanup(recordsToDeleteCount, maxSize);
        } else {
            logger.info("No cleanup needed. Total logs ({}) are within the max size limit ({}).", totalLogs, maxSize);
        }
    }

    /**
     * Performs the actual cleanup operation and logs the event.
     *
     * @param recordsToDeleteCount The number of records to delete.
     * @param maxSize              The maximum number of records to keep.
     */
    private void performCleanup(long recordsToDeleteCount, int maxSize) {
        logger.info("Found {} logs to delete to enforce the max size limit.", recordsToDeleteCount);

        List<Long> oldestLogIds = auditLogRepository.findOldestAuditLogIds((int) recordsToDeleteCount);
        auditLogRepository.deleteAllById(oldestLogIds);

        int deletedCount = oldestLogIds.size();
        logger.info("Scheduled job finished: Successfully deleted {} old audit log records.", deletedCount);

        logCleanupEvent(deletedCount, maxSize);
    }

    /**
     * Logs the audit log cleanup event to the audit trail.
     *
     * @param deletedCount The number of records that were deleted.
     * @param maxSize      The maximum size the audit log table is being capped at.
     */
    private void logCleanupEvent(int deletedCount, int maxSize) {
        Map<String, Object> details = new HashMap<>();
        details.put("message", String.format("Deleted %d oldest audit log records to enforce max size limit of %d.", deletedCount, maxSize));
        details.put("records_deleted", deletedCount);
        details.put("max_size", maxSize);
        eventPublisher.publishEvent(new AuditLogEvent(this, "System", null, EventType.AUDIT_LOG_CLEANUP, details));
    }

    /**
     * Retrieves the maximum audit log size from the application configuration service, with a fallback.
     *
     * @return The configured max size or the default value.
     */
    private int getMaxSize() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.AUDIT_LOG_MAX_SIZE))
                .map(Integer::parseInt)
                .orElse(DEFAULT_MAX_SIZE);
    }
}
