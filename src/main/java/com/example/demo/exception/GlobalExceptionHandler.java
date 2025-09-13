package com.example.demo.exception;

import com.example.demo.audit.event.AuditLogEvent;
import com.example.demo.audit.service.CooldownService;
import com.example.demo.security.dto.ApiResponse;
import com.example.demo.security.service.IdentityService;
import com.example.demo.util.constants.ValidationMessages;
import com.example.demo.util.enums.EventType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handles all exceptions in the application, ensuring consistent JSON error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final IdentityService identityService;
    private final CooldownService cooldownService;

    private final ApplicationEventPublisher eventPublisher;

    public GlobalExceptionHandler(IdentityService identityService, CooldownService cooldownService, ApplicationEventPublisher eventPublisher) {
        this.identityService = identityService;
        this.cooldownService = cooldownService;
        this.eventPublisher = eventPublisher;
    }

    // --- Application-Specific Exception Handlers ---

    /**
     * Handles {@link AccessDeniedException} for authorization failures.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 403 Forbidden status.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        String username = identityService.getAuthenticatedUsername();
        String ipAddress = identityService.getClientIpAddress(servletRequest);

        if (cooldownService.canLog(EventType.ACCESS_DENIED, username, ipAddress)) {
            Map<String, Object> details = new HashMap<>();
            details.put("request_uri", request.getDescription(false));
            details.put("reason", ex.getMessage());
            eventPublisher.publishEvent(new AuditLogEvent(this, username, ipAddress, EventType.ACCESS_DENIED, details));
        }

        return buildErrorResponse(HttpStatus.FORBIDDEN, ValidationMessages.ACCESS_DENIED);
    }

    /**
     * Catches all other unhandled exceptions as a fallback.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllUncaughtException(Exception ex) {
        logger.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ValidationMessages.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles {@link BadCredentialsException} for authentication failures.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 400 Bad Request status.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex, NativeWebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ValidationMessages.INVALID_CREDENTIALS);
    }

    // --- Spring Security Exception Handlers ---

    /**
     * Handles {@link DataIntegrityViolationException} for database constraint errors.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 409 Conflict status.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.CONFLICT, ValidationMessages.DATABASE_ERROR);
    }

    /**
     * Handles {@link InvalidInputException} for custom business logic validation errors.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 400 Bad Request status.
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidInputException(InvalidInputException ex, NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        String username = identityService.getAuthenticatedUsername();
        String ipAddress = identityService.getClientIpAddress(servletRequest);

        if (cooldownService.canLog(EventType.INVALID_INPUT, username, ipAddress)) {
            logger.warn("Invalid input provided: {} For user: {}, ipAddress: {}", ex.getMessage(), username, ipAddress);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Validation Exception Handlers ---

    /**
     * Handles {@link MethodArgumentNotValidException} from @Valid annotation failures.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 400 status and detailed field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<ValidationError> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ValidationError error = new ValidationError();
                    error.setField(fieldError.getField());
                    error.setMessage(fieldError.getDefaultMessage());
                    return error;
                })
                .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.VALIDATION_FAILED, validationErrors);
    }

    /**
     * Handles {@link MissingServletRequestParameterException} when a required parameter is missing.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 400 Bad Request status.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("Required parameter '%s' is missing.", ex.getParameterName());
        ValidationError validationError = new ValidationError();
        validationError.setField(ex.getParameterName());
        validationError.setMessage(message);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ValidationMessages.VALIDATION_FAILED, Collections.singletonList(validationError));
    }

    // --- Fallback Handler ---

    /**
     * Handles {@link ResourceNotFoundException} for 404 Not Found errors.
     *
     * @param ex The caught exception.
     * @return A structured {@link ApiResponse} with a 404 status.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // --- Helper Methods ---

    /**
     * A helper method to create a standardized error ResponseEntity.
     *
     * @param status  The HTTP status to set in the response.
     * @param message The user-friendly error message.
     * @return A ResponseEntity containing the structured error response.
     */
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message) {
        return buildErrorResponse(status, message, null);
    }

    /**
     * A helper method to create a standardized error ResponseEntity.
     *
     * @param status  The HTTP status to set in the response.
     * @param message The user-friendly error message.
     * @param errors  The validation errors.
     * @return A ResponseEntity containing the structured error response.
     */
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(HttpStatus status, String message, List<ValidationError> errors) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(status.value(), message, errors));
    }
}
