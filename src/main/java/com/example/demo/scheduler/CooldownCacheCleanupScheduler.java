package com.example.demo.scheduler;

import com.example.demo.audit.service.CooldownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A scheduled task that periodically clears the in-memory cache of the CooldownService.
 * This prevents the cache from growing indefinitely and ensures that cooldown periods
 * are eventually reset, even for long-dormant actors.
 */
@Component
public class CooldownCacheCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(CooldownCacheCleanupScheduler.class);

    private final CooldownService cooldownService;

    public CooldownCacheCleanupScheduler(CooldownService cooldownService) {
        this.cooldownService = cooldownService;
    }

    /**
     * Clears the cooldown service's internal cache at a scheduled interval.
     * The cron expression is configured in application.properties.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupCache() {
        logger.info("Starting scheduled job: Removing stale entries from CooldownService cache.");
        cooldownService.removeStaleEntries();
        logger.info("Scheduled job finished: Stale entries have been removed from CooldownService cache.");
    }
}
