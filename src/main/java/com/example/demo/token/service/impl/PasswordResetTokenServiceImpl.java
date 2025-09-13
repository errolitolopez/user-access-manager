package com.example.demo.token.service.impl;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.security.dto.PasswordResetDto;
import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.token.repository.PasswordResetTokenRepository;
import com.example.demo.token.service.PasswordResetTokenService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.TokenGenerator;
import com.example.demo.util.enums.EventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final static int DEFAULT_TOKEN_EXPIRATION_MINUTES = 15;
    private final static String DEFAULT_TOKEN_TYPE = "alphanumeric";

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    private final ApplicationConfigService applicationConfigService;

    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository,
                                         UserRepository userRepository,
                                         ApplicationConfigService applicationConfigService,
                                         ApplicationEventPublisher eventPublisher,
                                         PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.applicationConfigService = applicationConfigService;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new password reset token for a given user.
     *
     * @param user The user for whom the token is being created.
     * @return The created PasswordResetToken entity.
     */
    @Transactional
    @Override
    public PasswordResetToken createToken(User user) {
        checkAccountStatus(user);

        List<PasswordResetTokenStatus> activeStatuses = Arrays.asList(PasswordResetTokenStatus.PENDING, PasswordResetTokenStatus.SENT);
        List<PasswordResetToken> existingTokens = passwordResetTokenRepository.findByUserAndExpiryDateAfterAndStatusIn(user, LocalDateTime.now(), activeStatuses);

        if (!existingTokens.isEmpty()) {
            throw new InvalidInputException("An email has already been sent. Please check your inbox or wait for the token to expire.");
        }

        String token;
        if (getPasswordResetTokenType().equalsIgnoreCase("alphanumeric")) {
            token = TokenGenerator.generateAlphanumericToken();
        } else {
            token = TokenGenerator.generateNumeric6DigitToken();
        }

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(getPasswordResetTokenExpirationMinutes()));
        passwordResetToken.setStatus(PasswordResetTokenStatus.PENDING);

        PasswordResetToken savedToken = passwordResetTokenRepository.save(passwordResetToken);

        Map<String, Object> details = new HashMap<>();
        details.put("userId", savedToken.getUser().getId());
        details.put("username", savedToken.getUser().getUsername());
        details.put("token_id", savedToken.getId());
        publishAuditLogEvent(savedToken.getUser().getUsername(), EventType.CREATE_PASSWORD_RESET_TOKEN, details);

        return savedToken;
    }

    /**
     * Resets the user's password using a valid token and new password.
     *
     * @param passwordResetDto DTO containing the token and new password.
     */
    @Transactional
    @Override
    public void resetPassword(PasswordResetDto passwordResetDto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(passwordResetDto.getToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid password reset token."));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.deleteById(resetToken.getId());
            throw new InvalidInputException("Password reset token has expired.");
        }

        if (resetToken.getStatus() != PasswordResetTokenStatus.SENT) {
            throw new InvalidInputException("Invalid password reset token.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        user.setPasswordLastUpdated(LocalDateTime.now());
        user.setCredentialsExpired(false);
        userRepository.save(user);

        passwordResetTokenRepository.deleteById(resetToken.getId());

        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        publishAuditLogEvent(user.getUsername(), EventType.PASSWORD_RESET, details);
    }

    /**
     * Publishes an audit log event with standardized details.
     *
     * @param eventType The type of event.
     * @param details   The map of details for the event.
     */
    private void publishAuditLogEvent(String username, EventType eventType, Map<String, Object> details) {
        eventPublisher.publishEvent(new AuditLogEvent(this, username, null, eventType, details));
    }

    /**
     * Retrieves the password reset token expiration hours from the application config or uses a default value.
     *
     * @return The configured expiration minutes.
     */
    private int getPasswordResetTokenExpirationMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.PASSWORD_RESET_TOKEN_EXPIRATION_MINUTES))
                .map(Integer::parseInt)
                .orElse(DEFAULT_TOKEN_EXPIRATION_MINUTES);
    }

    /**
     * Retrieves the password reset token type from the application config or uses a default value.
     *
     * @return The configured token type.
     */
    private String getPasswordResetTokenType() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.PASSWORD_RESET_TOKEN_TYPE))
                .orElse(DEFAULT_TOKEN_TYPE);
    }

    private void checkAccountStatus(User user) {
        if (Boolean.TRUE.equals(user.getAccountExpired())) {
            throw new InvalidInputException("Your account has expired. Please contact support to renew it.");
        }
        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new InvalidInputException("Your account has been disabled. Please contact the administrator for assistance.");
        }
        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new InvalidInputException("Your account is locked. Please contact support for assistance.");
        }
    }
}
