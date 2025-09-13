package com.example.demo.security;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.audit.service.CooldownService;
import com.example.demo.security.service.CustomUserDetailsService;
import com.example.demo.security.service.IdentityService;
import com.example.demo.security.service.JwtService;
import com.example.demo.util.enums.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Filters incoming requests to validate JWTs and authenticate users.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final IdentityService identityService;
    private final CustomUserDetailsService userDetailsService;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;
    private final CooldownService cooldownService;

    public JwtAuthenticationFilter(JwtService jwtService, IdentityService identityService, CustomUserDetailsService userDetailsService, ApplicationEventPublisher publisher, ObjectMapper objectMapper, CooldownService cooldownService) {
        this.jwtService = jwtService;
        this.identityService = identityService;
        this.userDetailsService = userDetailsService;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.cooldownService = cooldownService;
    }


    /**
     * Processes each request to check for and validate a JWT token.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        String username = null;
        String ipAddress = identityService.getClientIpAddress(request);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        Optional<String> usernameOptional = jwtService.extractUsernameFromToken(request);

        // JWT token is malformed or expired
        if (usernameOptional.isEmpty()) {
            Map<String, Object> details = new HashMap<>();
            details.put("request_uri", request.getRequestURI());
            details.put("reason", "Invalid or expired JWT token");

            if (cooldownService.canLog(EventType.AUTHENTICATION_FAILURE, username, ipAddress)) {
                publisher.publishEvent(new AuditLogEvent(this, username, ipAddress, EventType.AUTHENTICATION_FAILURE, details));
            }

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            objectMapper.writeValue(response.getWriter(), Map.of("error", "Unauthorized", "message", "Invalid or expired JWT token."));
            return;
        }

        username = usernameOptional.get();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
