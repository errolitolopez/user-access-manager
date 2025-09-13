package com.example.demo.scheduler;

import com.example.demo.audit.event.BatchAuditLogEvent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A scheduled task to automatically expire user accounts after a certain period of time.
 */
@Component
public class AccountExpirationScheduler {
    private static final Logger logger = LoggerFactory.getLogger(AccountExpirationScheduler.class);

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AccountExpirationScheduler(UserRepository userRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Periodically checks all users and sets their accounts as expired if their account expiration date has passed.
     * This task runs daily at midnight.
     */
    @Scheduled(cron = "${scheduler.account.expiration.cron:0 0 0 * * *}")
    @Transactional
    public void expireUserAccounts() {
        logger.info("Starting scheduled job: Expiring user accounts.");

        LocalDateTime expirationDate = LocalDateTime.now();
        List<User> usersToExpire = userRepository.findAllByAccountExpirationDateBeforeAndAccountExpiredIsFalse(expirationDate);

        if (usersToExpire.isEmpty()) {
            logger.info("No user accounts to expire at this time.");
            return;
        }

        // Process users and prepare audit log details
        List<Map<String, Object>> auditDetailsList = usersToExpire.stream()
                .map(user -> {
                    user.setAccountExpired(true);
                    Map<String, Object> details = new HashMap<>();
                    details.put("userId", user.getId());
                    details.put("username", user.getUsername());
                    return details;
                })
                .collect(Collectors.toList());

        userRepository.saveAll(usersToExpire);

        eventPublisher.publishEvent(new BatchAuditLogEvent(this, "System", null, EventType.ACCOUNT_EXPIRED, auditDetailsList));

        logger.info("Scheduled job finished: Expired {} user accounts.", usersToExpire.size());
    }
}
