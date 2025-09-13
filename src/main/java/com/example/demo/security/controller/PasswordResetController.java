package com.example.demo.security.controller;

import com.example.demo.security.dto.ApiResponse;
import com.example.demo.security.dto.PasswordResetDto;
import com.example.demo.security.dto.PasswordResetRequestDto;
import com.example.demo.token.service.PasswordResetTokenService;
import com.example.demo.user.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling all password reset functionality.
 * This controller provides public endpoints to request, verify, and complete a password reset.
 */
@RestController
@RequestMapping("/api/public/password-reset")
@Validated
public class PasswordResetController {

    private final UserRepository userRepository;
    private final PasswordResetTokenService passwordResetTokenService;

    public PasswordResetController(UserRepository userRepository, PasswordResetTokenService passwordResetTokenService) {
        this.userRepository = userRepository;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    /**
     * Handles the request to initiate a password reset.
     * This endpoint is public and is the first step in the password reset process.
     *
     * @param requestDto DTO containing the user's email.
     * @return A ResponseEntity with a success message.
     */
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(passwordResetTokenService::createToken);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "If an account with that email exists, a password reset link has been sent."));
    }

    /**
     * Resets the user's password using a valid token.
     *
     * @param passwordResetDto DTO containing the token and the new password.
     * @return A ResponseEntity with a success message.
     */
    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid PasswordResetDto passwordResetDto) {
        passwordResetTokenService.resetPassword(passwordResetDto);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Your password has been successfully reset."));
    }
}
