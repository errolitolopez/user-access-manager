package com.example.demo.scheduler;

import com.example.demo.audit.event.BatchAuditLogEvent;
import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.token.repository.PasswordResetTokenRepository;
import com.example.demo.user.entity.User;
import com.example.demo.util.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A scheduled task to clean up old password reset tokens from the database.
 * This removes tokens that are either expired or have already been processed,
 * ensuring database hygiene and preventing potential security issues from old tokens.
 */
@Component
public class PasswordResetTokenCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetTokenCleanupScheduler.class.getName());

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PasswordResetTokenCleanupScheduler(PasswordResetTokenRepository passwordResetTokenRepository,
                                              ApplicationEventPublisher eventPublisher) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Periodically checks for and deletes expired password reset tokens.
     * The cron expression is configured in application.properties.
     */
    @Scheduled(cron = "${scheduler.password.reset.cleanup.cron:0 0 * * * *}")
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("Starting scheduled job: Cleaning up expired password reset tokens.");
        List<PasswordResetToken> tokensToDelete = passwordResetTokenRepository.findByExpiryDateBefore(LocalDateTime.now());

        if (tokensToDelete.isEmpty()) {
            return;
        }

        List<Map<String, Object>> auditDetailsList = tokensToDelete.stream()
                .map(token -> new HashMap<String, Object>() {{
                    put("tokenId", token.getId());
                    put("username", Optional.ofNullable(token.getUser()).map(User::getUsername).orElse("N/A"));
                    put("reason", "Token has expired");
                }})
                .collect(Collectors.toList());

        passwordResetTokenRepository.deleteAll(tokensToDelete);
        eventPublisher.publishEvent(new BatchAuditLogEvent(this, "System", null, EventType.PASSWORD_RESET_TOKEN_CLEANUP, auditDetailsList));
        logger.info("Scheduled job finished: Successfully deleted {} expired password reset tokens.", auditDetailsList.size());
    }
}
