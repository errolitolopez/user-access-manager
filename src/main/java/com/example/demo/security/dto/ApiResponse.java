package com.example.demo.security.dto;

import com.example.demo.exception.ValidationError;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.List;

/**
 * A generic API response DTO for a consistent success and error responses.
 *
 * @param <T> The type of the data payload for success responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    /** The HTTP status code of the response. */
    private Integer status;

    /** A user-friendly message explaining the outcome of the request. */
    private String message;

    /** The data payload for a successful response. */
    private T data;

    /** A list of specific field-level validation errors for a failure response. */
    private List<ValidationError> errors;

    public ApiResponse() {
    }

    private ApiResponse(Integer status, String message, T data, List<ValidationError> errors) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    /**
     * Creates a new success API response with a message and a data payload.
     *
     * @param message The message for the response.
     * @param data    The data payload to include.
     * @param <T>     The type of the data payload.
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> success(Integer status, String message, T data) {
        return new ApiResponse<>(status, message, data, null);
    }

    /**
     * Creates a new success API response for operations that do not return a data payload (e.g., delete, update).
     *
     * @param message The message for the response.
     * @param <T>     The type of the data payload (will be null).
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> success(Integer status, String message) {
        return new ApiResponse<>(status, message, null, null);
    }

    /**
     * Creates a new error API response with an error message and no data.
     *
     * @param status  The HTTP status code.
     * @param message The user-friendly error message.
     * @param <T>     The type of the data payload (will be null).
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> error(Integer status, String message) {
        return new ApiResponse<>(status, message, null, null);
    }

    /**
     * Creates a new error API response with an error message and a list of detailed validation errors.
     *
     * @param status  The HTTP status code.
     * @param message The general error message.
     * @param errors  A list of detailed validation errors.
     * @param <T>     The type of the data payload (will be null).
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> error(Integer status, String message, List<ValidationError> errors) {
        return new ApiResponse<>(status, message, null, errors);
    }

    // Getters and Setters
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<ValidationError> getErrors() {
        return errors;
    }

    public void setErrors(List<ValidationError> errors) {
        this.errors = errors;
    }
}
