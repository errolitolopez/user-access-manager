package com.example.demo.exception;

/**
 * Represents a specific validation error for a single input field.
 * This is typically used within a larger ErrorResponse object to provide
 * detailed feedback on what went wrong with the input.
 */
public class ValidationError {

    /** The name of the field that failed validation. */
    private String field;

    /** The specific error message for the field. */
    private String message;

    // Getters and Setters

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ValidationError{" +
                "field='" + field + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

