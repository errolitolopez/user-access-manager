package com.example.demo.scheduler;

import com.example.demo.audit.event.BatchAuditLogEvent;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.user.entity.User;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.util.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A scheduled task to manage user account and credential expiration.
 */
@Component
public class UserCredentialScheduler {
    private static final Logger logger = LoggerFactory.getLogger(UserCredentialScheduler.class);
    private static final int DEFAULT_CREDENTIAL_EXPIRATION_DAYS = 90;

    private final UserRepository userRepository;
    private final ApplicationConfigService applicationConfigService;
    private final ApplicationEventPublisher eventPublisher;

    public UserCredentialScheduler(UserRepository userRepository,
                                   ApplicationConfigService applicationConfigService,
                                   ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.applicationConfigService = applicationConfigService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Periodically checks all users and sets their credentials as expired if their password has not been updated within a specified number of days.
     * This task runs daily at midnight.
     */
    @Scheduled(cron = "${scheduler.credentials.cron:0 0 0 * * *}")
    @Transactional
    public void expireUserCredentials() {
        logger.info("Starting scheduled job: Expiring user credentials.");
        int credentialExpirationDays = getCredentialExpirationDays();
        LocalDateTime expirationDate = LocalDateTime.now().minusDays(credentialExpirationDays);
        List<User> usersToExpire = userRepository.findAllByPasswordLastUpdatedBeforeAndCredentialsExpiredIsFalse(expirationDate);

        if (usersToExpire.isEmpty()) {
            return;
        }

        List<Map<String, Object>> auditDetailsList = processAndPrepareForAudit(usersToExpire);

        userRepository.saveAll(usersToExpire);

        eventPublisher.publishEvent(new BatchAuditLogEvent(this, "System", null, EventType.CREDENTIALS_EXPIRED, auditDetailsList));

        logger.info("Scheduled job finished: Expired {} user credentials.", usersToExpire.size());
    }

    /**
     * Processes the list of users to expire, updating their credentials status and preparing a list of audit details.
     *
     * @param usersToExpire The list of user entities whose credentials are to be expired.
     * @return A list of maps containing details for the audit log.
     */
    private List<Map<String, Object>> processAndPrepareForAudit(List<User> usersToExpire) {
        List<Map<String, Object>> auditDetailsList = new ArrayList<>();
        for (User user : usersToExpire) {
            user.setCredentialsExpired(true);
            auditDetailsList.add(Map.of("userId", user.getId(), "username", user.getUsername()));
        }
        return auditDetailsList;
    }

    /**
     * Retrieves the credential expiration days from the application configuration service, with a fallback.
     *
     * @return The configured number of days or the default value.
     */
    private int getCredentialExpirationDays() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.CREDENTIAL_EXPIRATION_DAYS))
                .map(Integer::parseInt)
                .orElse(DEFAULT_CREDENTIAL_EXPIRATION_DAYS);
    }
}
