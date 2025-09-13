package com.example.demo.scheduler;

import com.example.demo.config.service.ApplicationConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A scheduled task that periodically refreshes the in-memory cache of
 * application configurations from the database.
 * This ensures the application reflects any changes made to the configuration
 * table outside the application's API.
 */
@Component
public class ApplicationConfigCacheScheduler {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigCacheScheduler.class);

    private final ApplicationConfigService applicationConfigService;

    public ApplicationConfigCacheScheduler(ApplicationConfigService applicationConfigService) {
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * This method is triggered when the application has fully started.
     * It ensures the configuration cache is populated immediately,
     * so all services have access to the latest settings from the very beginning.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Application started. Forcing immediate cache refresh for application configurations.");
        refreshConfigCache();
        logger.info("Initial application configuration cache refresh completed.");
    }

    /**
     * Periodically refreshes the application configuration cache.
     * The cron expression is configured in application.properties.
     */
    @Scheduled(cron = "${scheduler.config.refresh.cron:0 */15 * * * *}")
    public void refreshConfigCache() {
        logger.info("Starting scheduled cache refresh for application configurations.");
        applicationConfigService.refreshCache();
        logger.info("Finished scheduled cache refresh.");
    }
}
