package com.example.demo.security;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.audit.service.CooldownService;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.security.service.IdentityService;
import com.example.demo.security.service.JwtService;
import com.example.demo.security.service.RateLimitingService;
import com.example.demo.util.enums.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

/**
 * A Spring Web filter to enforce API rate limits on a configurable set of URLs.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;
    private final IdentityService identityService;
    private final JwtService jwtService;
    private final ApplicationConfigService applicationConfigService;
    private final CooldownService cooldownService;

    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimitingService rateLimitingService,
                              IdentityService identityService,
                              JwtService jwtService,
                              ApplicationConfigService applicationConfigService,
                              CooldownService cooldownService,
                              ApplicationEventPublisher eventPublisher,
                              ObjectMapper objectMapper) {
        this.rateLimitingService = rateLimitingService;
        this.identityService = identityService;
        this.jwtService = jwtService;
        this.applicationConfigService = applicationConfigService;
        this.cooldownService = cooldownService;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String username = jwtService.extractUsernameFromToken(request).orElse(null);
        String requestUri = request.getRequestURI();
        String ipAddress = identityService.getClientIpAddress(request);
        String identifier = (username != null ? username + ":" : "") + ipAddress + ":" + requestUri;

        boolean isRateLimitEnabled = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.RATE_LIMIT_ENABLED)).map(Boolean::parseBoolean).orElse(false);
        if (isRateLimitEnabled) {

            List<String> includedUrls = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.RATE_LIMIT_INCLUDED_URLS)).map(s -> Arrays.asList(s.split(","))).orElse(Collections.emptyList());
            List<String> excludedUrlSuffixes = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.RATE_LIMIT_EXCLUDED_URLS)).map(s -> Arrays.asList(s.split(","))).orElse(Collections.emptyList());

            if (isRateLimitedUrl(requestUri, includedUrls) && !isExcludedUrl(requestUri, excludedUrlSuffixes)) {
                if (!rateLimitingService.tryConsume(identifier)) {
                    if (cooldownService.canLog(EventType.TOO_MANY_REQUESTS, identifier, requestUri)) {
                        Map<String, Object> details = new HashMap<>();
                        details.put("request_uri", requestUri);
                        details.put("reason", "API rate limit exceeded");
                        eventPublisher.publishEvent(new AuditLogEvent(this, username, ipAddress, EventType.TOO_MANY_REQUESTS, details));
                    }
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json");

                    Map<String, String> data = new HashMap<>();
                    data.put("error", "Too Many Requests");
                    data.put("message", "You have exceeded the API request limit. Please try again later.");
                    objectMapper.writeValue(response.getWriter(), data);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Checks if a given URL is in the list of URLs to be rate-limited.
     *
     * @param requestUri      The URI to check.
     * @param rateLimitedUrls The list of URL prefixes to be rate-limited.
     * @return {@code true} if the URL is rate-limited, {@code false} otherwise.
     */
    private boolean isRateLimitedUrl(String requestUri, List<String> rateLimitedUrls) {
        return rateLimitedUrls.stream().anyMatch(requestUri::startsWith);
    }

    /**
     * Checks if a given URL is in the list of URLs to be excluded from rate-limiting.
     *
     * @param requestUri          The URI to check.
     * @param excludedUrlSuffixes The list of URL suffixes to be excluded.
     * @return {@code true} if the URL should be excluded, {@code false} otherwise.
     */
    private boolean isExcludedUrl(String requestUri, List<String> excludedUrlSuffixes) {
        return excludedUrlSuffixes.stream().anyMatch(requestUri::endsWith);
    }
}
