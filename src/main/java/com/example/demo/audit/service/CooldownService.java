package com.example.demo.audit.service;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.util.enums.EventType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service to manage logging with a cooldown period for specific event types.
 * It prevents excessive logging from the same actor (user or IP) and event type,
 * which is useful for filtering out log spam from repetitive actions like
 * failed login attempts or brute-force attacks.
 */
@Service
public class CooldownService {
    private static final int AUDIT_LOG_COOLDOWN_MINUTES_DEFAULT = 5;
    private final ConcurrentHashMap<String, LocalDateTime> lastLoggedTime = new ConcurrentHashMap<>();

    private final ApplicationConfigService applicationConfigService;

    public CooldownService(ApplicationConfigService applicationConfigService) {
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * Checks if an event can be logged based on a cooldown period.
     * This prevents a single actor from spamming the audit log with the same event type.
     *
     * @param eventType The type of event.
     * @param keys      A variable number of strings to form the unique identifier for the actor.
     * @return {@code true} if the event can be logged, {@code false} otherwise.
     */
    public boolean canLog(EventType eventType, String... keys) {
        String key = createKey(eventType, keys);
        LocalDateTime lastLoggedTime = this.lastLoggedTime.get(key);
        long cooldownMinutes = getCooldownMinutes();

        if (lastLoggedTime == null || Duration.between(lastLoggedTime, LocalDateTime.now()).toMinutes() >= cooldownMinutes) {
            this.lastLoggedTime.put(key, LocalDateTime.now());
            return true;
        }
        return false;
    }

    /**
     * Removes entries from the in-memory cooldown cache that are past their due date.
     * This is typically used by a scheduled task to prevent the cache from growing indefinitely.
     */
    public void removeStaleEntries() {
        long cooldownMinutes = getCooldownMinutes();
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(cooldownMinutes);

        // This is a thread-safe way to remove entries from a ConcurrentHashMap
        this.lastLoggedTime.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoffTime));
    }

    /**
     * Retrieves the configured cooldown period for audit logging, with a fallback to a default value.
     *
     * @return The configured or default cooldown period in minutes.
     */
    private int getCooldownMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.AUDIT_LOG_COOLDOWN_MINUTES))
                .map(Integer::parseInt)
                .orElse(AUDIT_LOG_COOLDOWN_MINUTES_DEFAULT);
    }

    /**
     * Creates a unique, non-null key for the cooldown cache.
     *
     * @param eventType The type of the event.
     * @param keys      The keys used to form the unique identifier.
     * @return A unique string representing the combination of the keys and event type.
     */
    private String createKey(EventType eventType, String... keys) {
        return Stream.of(keys)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(":", "", ":" + eventType.name()));
    }
}
