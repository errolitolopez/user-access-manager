package com.example.demo.exception;

/**
 * Custom exception to be thrown when a user provides invalid or null input.
 */
public class InvalidInputException extends RuntimeException {

    public InvalidInputException(String message) {
        super(message);
    }
}
