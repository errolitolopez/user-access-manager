package com.example.demo.security.service;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Service for managing API request rate limits using Resilience4j.
 * This implementation provides a per-user rate limit using the token bucket algorithm.
 * It dynamically retrieves configuration values from the {@link ApplicationConfigService},
 * allowing for runtime changes without a service restart.
 */
@Service
public class RateLimitingService {
    private final RateLimiterRegistry rateLimiterRegistry;
    private final ApplicationConfigService applicationConfigService;

    public RateLimitingService(RateLimiterRegistry rateLimiterRegistry, ApplicationConfigService applicationConfigService) {
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * Attempts to consume a token for a given user identified by a unique key.
     * This method retrieves the rate limit capacity and refill duration from the
     * dynamic application configuration before attempting to acquire a permission.
     *
     * @param identifier The unique identifier for the user (e.g., username, IP address).
     * @return {@code true} if the request is allowed, {@code false} if the rate limit is exceeded.
     */
    public boolean tryConsume(String identifier) {
        // Dynamically fetch configuration values with fallbacks to hardcoded defaults.
        int capacity = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.RATE_LIMIT_CAPACITY))
                .map(Integer::parseInt)
                .orElse(500); // Default to 500 if the config is not found or is invalid.

        long refillDurationMinutes = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.RATE_LIMIT_REFILL_DURATION_MINUTES))
                .map(Long::parseLong)
                .orElse(1L); // Default to 1 minute if the config is not found or is invalid.

        // Create a custom rate limiter configuration on the fly.
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(identifier, () -> RateLimiterConfig.custom()
                .limitForPeriod(capacity)
                .limitRefreshPeriod(Duration.ofMinutes(refillDurationMinutes))
                .timeoutDuration(Duration.ofSeconds(0))
                .build());

        // Attempt to acquire one permission (token) from the rate limiter.
        // This is the core logic that checks and enforces the rate limit.
        return rateLimiter.acquirePermission(1);
    }
}
