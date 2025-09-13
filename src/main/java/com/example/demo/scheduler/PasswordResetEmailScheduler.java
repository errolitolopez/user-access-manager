package com.example.demo.scheduler;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.mail.dto.SmtpConfigDto;
import com.example.demo.mail.service.MailService;
import com.example.demo.mail.service.SmtpConfigService;
import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.token.repository.PasswordResetTokenRepository;
import com.example.demo.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A scheduled task to send password reset emails for unprocessed requests.
 * This decouples the email sending process from the user's request,
 * improving performance and providing a more robust email delivery system.
 */
@Component
public class PasswordResetEmailScheduler {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetEmailScheduler.class);

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    private final ApplicationConfigService applicationConfigService;
    private final MailService mailService;
    private final SmtpConfigService smtpConfigService;

    public PasswordResetEmailScheduler(PasswordResetTokenRepository passwordResetTokenRepository, ApplicationConfigService applicationConfigService, MailService mailService, SmtpConfigService smtpConfigService) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.applicationConfigService = applicationConfigService;
        this.mailService = mailService;
        this.smtpConfigService = smtpConfigService;
    }

    /**
     * Periodically checks the database for pending password reset tokens
     * and sends a password reset email for each one.
     */
    @Scheduled(cron = "${scheduler.password_reset_email.cron:0 * * * * ?}")
    @Transactional
    public void sendPendingPasswordResetEmails() {
        logger.info("Starting scheduled job: Sending pending password reset emails.");

        List<SmtpConfigDto> enabledConfigs = smtpConfigService.getAllEnabledConfigs();
        if (enabledConfigs.isEmpty()) {
            logger.warn("No enabled SMTP configurations found. Skipping email sending.");
            return;
        }

        String passwordResetBaseUrl = applicationConfigService.getValue(ApplicationConfigKeys.PASSWORD_RESET_BASE_URL);
        if (passwordResetBaseUrl == null || !UrlUtil.isValidUrl(passwordResetBaseUrl)) {
            logger.error("Password reset base URL is not configured or is invalid. Skipping email sending.");
            return;
        }

        List<PasswordResetToken> pendingTokens = passwordResetTokenRepository
                .findAllByStatus(PasswordResetTokenStatus.PENDING);
        if (pendingTokens.isEmpty()) {
            return;
        }
        logger.info("Found {} pending tokens to process.", pendingTokens.size());
        mailService.sendPasswordResetEmail(passwordResetBaseUrl, pendingTokens);
        logger.info("Scheduled job finished: Processed {} password reset tokens.", pendingTokens.size());
    }
}
