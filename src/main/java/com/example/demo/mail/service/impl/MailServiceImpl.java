package com.example.demo.mail.service.impl;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.mail.dto.SmtpConfigDto;
import com.example.demo.mail.service.MailService;
import com.example.demo.mail.service.SmtpConfigService;
import com.example.demo.token.entity.PasswordResetToken;
import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.token.repository.PasswordResetTokenRepository;
import com.example.demo.util.enums.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service implementation for sending emails.
 */
@Service
public class MailServiceImpl implements MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    private final static int DEFAULT_TOKEN_EXPIRATION_MINUTES = 15;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final ApplicationConfigService applicationConfigService;
    private final SmtpConfigService smtpConfigService;

    private final ApplicationEventPublisher eventPublisher;

    public MailServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository, ApplicationConfigService applicationConfigService, SmtpConfigService smtpConfigService, ApplicationEventPublisher eventPublisher) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.applicationConfigService = applicationConfigService;
        this.smtpConfigService = smtpConfigService;
        this.eventPublisher = eventPublisher;
    }


    @Override
    @Async
    @Transactional
    public void sendPasswordResetEmail(String passwordResetBaseUrl, List<PasswordResetToken> pendingTokens) {
        List<SmtpConfigDto> enabledConfigs = smtpConfigService.getAllEnabledConfigs();
        if (enabledConfigs.isEmpty()) {
            return;
        }

        int expirationMinutes = getPasswordResetTokenExpirationMinutes();

        for (PasswordResetToken token : pendingTokens) {
            for (SmtpConfigDto smtpConfig : enabledConfigs) {
                String configName = smtpConfig.getName();
                String to = token.getUser().getEmail();

                if (smtpConfig.getEnabled() == false) {
                    logger.warn("SMTP configuration '{}' is disabled.", configName);
                    continue;
                }

                JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
                mailSender.setHost(smtpConfig.getHost());
                mailSender.setPort(smtpConfig.getPort());
                mailSender.setUsername(smtpConfig.getUsername());
                mailSender.setPassword(smtpConfig.getPassword());

                Properties props = mailSender.getJavaMailProperties();
                props.put("mail.smtp.auth", smtpConfig.getSmtpAuth());
                props.put("mail.smtp.starttls.enable", smtpConfig.getStarttlsEnabled());

                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(smtpConfig.getUsername());
                message.setTo(to);
                message.setSubject("Password Reset Request");
                message.setText("To reset your password, click the following link: " + passwordResetBaseUrl + "/" + token.getToken());
                try {
                    mailSender.send(message);
                    // Increment the sent count on success.
                    token.setStatus(PasswordResetTokenStatus.SENT);
                    token.setExpiryDate(LocalDateTime.now().plusMinutes(expirationMinutes));
                    passwordResetTokenRepository.save(token);

                    smtpConfigService.incrementSentCount(configName);

                    Map<String, Object> details = new HashMap<>();
                    details.put("userId", token.getUser().getId());
                    details.put("username", token.getUser().getUsername());
                    details.put("smtpConfigName", configName);
                    eventPublisher.publishEvent(new AuditLogEvent(this, "System", null, EventType.PASSWORD_RESET_EMAIL_SENT, details));
                    break;
                } catch (MailException e) {
                    logger.error("Failed to send password reset email to {} with SMTP configuration '{}': {}", to, configName, e.getMessage());
                    smtpConfigService.updateEnabledStatus(smtpConfig.getId(), false);
                }
            }
        }
    }

    /**
     * Retrieves the password reset token expiration minutes from the application config or uses a default value.
     *
     * @return The configured expiration minutes.
     */
    private int getPasswordResetTokenExpirationMinutes() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.PASSWORD_RESET_TOKEN_EXPIRATION_MINUTES))
                .map(Integer::parseInt)
                .orElse(DEFAULT_TOKEN_EXPIRATION_MINUTES);
    }
}
