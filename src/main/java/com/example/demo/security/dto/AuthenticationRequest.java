package com.example.demo.security.dto;

/**
 * Data Transfer Object (DTO) for handling user authentication requests.
 * <p>
 * This class is a simple Plain Old Java Object (POJO) used to encapsulate
 * the username and password submitted by a user during the login process.
 * It's designed to be used as the request body in an authentication endpoint.
 * </p>
 */
public class AuthenticationRequest {

    private String username;
    private String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
