package com.example.demo.token.entity;

import com.example.demo.token.enums.PasswordResetTokenStatus;
import com.example.demo.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a temporary token used for password reset functionality.
 * This entity is linked to a user and has an expiration date for security.
 */
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    /**
     * The unique identifier for the password reset token record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique, non-guessable token string.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The expiration date and time for the token.
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * The current status of the token (e.g., PENDING, USED, EXPIRED).
     */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PasswordResetTokenStatus status;

    /**
     * The date and time when the token was created.
     */
    @CreationTimestamp
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    /**
     * The User entity associated with this token.
     * This is a one-to-one relationship using a foreign key.
     */
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public PasswordResetTokenStatus getStatus() {
        return status;
    }

    public void setStatus(PasswordResetTokenStatus status) {
        this.status = status;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
