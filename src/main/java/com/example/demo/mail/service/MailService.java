package com.example.demo.mail.service;

import com.example.demo.token.entity.PasswordResetToken;

import java.util.List;

/**
 * Service interface for sending emails.
 * It provides a clean contract for the email sending functionality.
 */
public interface MailService {

    void sendPasswordResetEmail(String passwordResetBaseUrl, List<PasswordResetToken> pendingTokens);
}
