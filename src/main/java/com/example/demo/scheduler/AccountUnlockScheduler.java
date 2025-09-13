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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A scheduled task to automatically unlock user accounts after a certain period of time.
 * This prevents permanent lockouts due to brute-force attempts.
 */
@Component
public class AccountUnlockScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AccountUnlockScheduler.class);
    private static final long DEFAULT_UNLOCK_TIME_MINUTES = 30;

    private final UserRepository userRepository;
    private final ApplicationConfigService applicationConfigService;
    private final ApplicationEventPublisher eventPublisher;

    public AccountUnlockScheduler(UserRepository userRepository, ApplicationConfigService applicationConfigService, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.applicationConfigService = applicationConfigService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * This scheduled job runs every 3 minutes to check for locked accounts.
     */
    @Scheduled(cron = "${scheduler.unlock.cron:0 */3 * * * *}")
    @Transactional
    public void unlockAccounts() {
        logger.info("Starting scheduled job: Unlocking accounts.");
        long unlockTimeMinutes = getUnlockTimeMinutes();
        LocalDateTime unlockThreshold = LocalDateTime.now().minusMinutes(unlockTimeMinutes);
        List<User> lockedUsers = userRepository.findAllByLastFailedLoginTimeBeforeAndAccountLockedIsTrue(unlockThreshold);

        if (lockedUsers.isEmpty()) {
            return;
        }

        List<Map<String, Object>> auditDetailsList = processUsersAndPrepareAudit(lockedUsers);

        userRepository.saveAll(lockedUsers);

        eventPublisher.publishEvent(new BatchAuditLogEvent(this, "System", null, EventType.ACCOUNT_UNLOCKED, auditDetailsList));

        logger.info("Scheduled job finished: {} accounts were unlocked.", lockedUsers.size());
    }

    /**
     * Iterates through the list of locked users, updates their properties,
     * and prepares a list of audit details for logging.
     *
     * @param lockedUsers The list of user entities to process.
     * @return A list of maps containing details for the audit log.
     */
    private List<Map<String, Object>> processUsersAndPrepareAudit(List<User> lockedUsers) {
        return lockedUsers.stream()
                .map(user -> {
                    user.setAccountLocked(false);
                    user.setFailedLoginAttempts(0);
                    user.setLastFailedLoginTime(null);
                    return Map.<String, Object>of("userId", user.getId(), "username", user.getUsername());
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the account unlock time from the application configuration service, with a fallback.
     *
     * @return The configured unlock time in minutes or the default value.
     */
    private long getUnlockTimeMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.ACCOUNT_UNLOCK_TIME_MINUTES))
                .map(Long::parseLong)
                .orElse(DEFAULT_UNLOCK_TIME_MINUTES);
    }
}
