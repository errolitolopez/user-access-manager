package com.example.demo.controller;

import com.example.demo.security.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * An abstract base class for all controllers to provide consistent
 * and simplified API response handling.
 *
 */
public abstract class BaseController {

    /**
     * Creates a standardized ResponseEntity for a successful request.
     *
     * @param status The HTTP status code.
     * @param message The success message.
     * @param data The data payload to be returned.
     * @param <T> The type of the data payload.
     * @return A ResponseEntity with the ApiResponse payload.
     */
    protected <T> ResponseEntity<ApiResponse<T>> buildSuccessResponse(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(ApiResponse.success(status.value(), message, data));
    }

    /**
     * Creates a standardized ResponseEntity for a successful request with no data payload.
     *
     * @param status The HTTP status code.
     * @param message The success message.
     * @return A ResponseEntity with the ApiResponse payload.
     */
    protected ResponseEntity<ApiResponse<Void>> buildSuccessResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponse.success(status.value(), message));
    }
}
