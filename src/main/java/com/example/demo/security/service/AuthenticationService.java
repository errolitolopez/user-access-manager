package com.example.demo.security.service;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.audit.service.CooldownService;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.security.dto.AuthenticationRequest;
import com.example.demo.security.dto.AuthenticationResponse;
import com.example.demo.user.dto.UserDto;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.constants.ValidationMessages;
import com.example.demo.util.enums.EventType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Service for handling user authentication and token generation.
 * It now uses dynamic application configurations for security policies.
 */
@Service
public class AuthenticationService {
    private static final int MAX_FAILED_LOGIN_ATTEMPTS_DEFAULT = 10;
    private static final int LOCKOUT_RESET_MINUTES_DEFAULT = 30;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ApplicationConfigService applicationConfigService;
    private final IdentityService identityService;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CooldownService cooldownService;

    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;

    public AuthenticationService(UserRepository userRepository,
                                 UserMapper userMapper,
                                 ApplicationConfigService applicationConfigService,
                                 IdentityService identityService,
                                 JwtService jwtService,
                                 CustomUserDetailsService customUserDetailsService,
                                 CooldownService cooldownService,
                                 AuthenticationManager authenticationManager,
                                 ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.applicationConfigService = applicationConfigService;
        this.identityService = identityService;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.cooldownService = cooldownService;
        this.authenticationManager = authenticationManager;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request     The authentication request with username and password.
     * @param httpRequest The HTTP request to retrieve client information.
     * @return An AuthenticationResponse containing the JWT token and user details.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpRequest) {
        final String ipAddress = identityService.getClientIpAddress(httpRequest);
        final String username = request.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    Map<String, Object> details = new HashMap<>();
                    details.put("reason", "Invalid username.");
                    details.put("username", username);
                    // The cooldown check is now inside the publishAuditLogEvent method
                    publishAuditLogEvent(null, ipAddress, EventType.AUTHENTICATION_FAILURE, details);
                    return new BadCredentialsException(ValidationMessages.USER_NOT_FOUND);
                });

        // Pre-authentication checks for account status
        checkAccountStatus(user);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, request.getPassword()));

            Map<String, Object> details = new HashMap<>();
            details.put("userId", user.getId());
            details.put("failedAttempts", user.getFailedLoginAttempts());
            publishAuditLogEvent(user.getUsername(), ipAddress, EventType.AUTHENTICATION_SUCCESS, details);

            resetFailedLoginAttempts(user);

        } catch (BadCredentialsException e) {
            handleFailedLoginAttempt(user, ipAddress);

            Map<String, Object> details = new HashMap<>();
            details.put("userId", user.getId());
            details.put("reason", "Invalid password.");
            publishAuditLogEvent(username, ipAddress, EventType.AUTHENTICATION_FAILURE, details);

            throw new BadCredentialsException(ValidationMessages.PASSWORD_MISMATCH);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String jwtToken = jwtService.generateToken(userDetails);
        UserDto userDto = userMapper.toDto(user);

        return new AuthenticationResponse(jwtToken, userDto);
    }

    /**
     * Checks the account status of the user and throws an exception if it's expired, disabled, or locked.
     *
     * @param user The user to check.
     */
    private void checkAccountStatus(User user) {
        if (Boolean.TRUE.equals(user.getAccountExpired())) {
            throw new InvalidInputException("Your account has expired. Please contact support to renew it.");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new InvalidInputException("Your account has been disabled. Please contact the administrator for assistance.");
        }
        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new InvalidInputException("Your account is locked due to too many failed login attempts. Please try again later.");
        }
    }

    /**
     * Handles a failed login attempt by incrementing the failed attempts count, and locking the account if the max limit is reached.
     *
     * @param user      The user who failed to log in.
     * @param ipAddress The IP address of the failed login attempt.
     */
    private void handleFailedLoginAttempt(User user, String ipAddress) {
        long lockoutResetMinutes = getLockoutResetMinutes();

        if (user.getLastFailedLoginTime() != null && ChronoUnit.MINUTES.between(user.getLastFailedLoginTime(), LocalDateTime.now()) >= lockoutResetMinutes) {
            user.setFailedLoginAttempts(0);
        }

        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        user.setLastFailedLoginTime(LocalDateTime.now());

        int maxFailedLoginAttempts = getMaxFailedLoginAttempts();
        if (user.getFailedLoginAttempts() >= maxFailedLoginAttempts) {
            user.setAccountLocked(true);
            Map<String, Object> details = new HashMap<>();
            details.put("userId", user.getId());
            details.put("failedAttempts", user.getFailedLoginAttempts());
            publishAuditLogEvent(user.getUsername(), ipAddress, EventType.ACCOUNT_LOCKED, details);
        }
        userRepository.save(user);
    }

    /**
     * Resets a user's failed login attempts and last failed login time upon successful authentication.
     *
     * @param user The user to update.
     */
    private void resetFailedLoginAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(null);
        userRepository.save(user);
    }

    /**
     * Publishes a new AuditLogEvent with a cooldown.
     *
     * @param username  The username associated with the event.
     * @param ipAddress The IP address associated with the event.
     * @param eventType The type of the event.
     * @param details   A map containing the event's details.
     */
    private void publishAuditLogEvent(String username, String ipAddress, EventType eventType, Map<String, Object> details) {
        if (cooldownService.canLog(eventType, username, ipAddress)) {
            eventPublisher.publishEvent(new AuditLogEvent(this, username, ipAddress, eventType, details));
        }
    }

    /**
     * Retrieves the configured maximum number of failed login attempts, with a fallback to a default value.
     *
     * @return The configured or default maximum number of failed login attempts.
     */
    private int getMaxFailedLoginAttempts() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.MAX_FAILED_LOGIN_ATTEMPTS))
                .map(Integer::parseInt)
                .orElse(MAX_FAILED_LOGIN_ATTEMPTS_DEFAULT);
    }

    /**
     * Retrieves the configured lockout reset period, with a fallback to a default value.
     *
     * @return The configured or default lockout reset period in minutes.
     */
    private long getLockoutResetMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.LOCKOUT_RESET_MINUTES))
                .map(Long::parseLong)
                .orElse((long) LOCKOUT_RESET_MINUTES_DEFAULT);
    }
}
