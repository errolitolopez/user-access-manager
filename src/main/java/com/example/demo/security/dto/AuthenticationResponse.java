package com.example.demo.security.dto;

import com.example.demo.user.dto.UserDto;

/**
 * Represents the response object for user authentication.
 * This class includes the JWT token and the user's details upon successful authentication.
 */
public class AuthenticationResponse {

    /**
     * The JWT token for the authenticated user.
     */
    private String jwtToken;

    /**
     * The user details associated with the JWT token.
     */
    private UserDto userDto;

    public AuthenticationResponse(String jwtToken, UserDto userDto) {
        this.jwtToken = jwtToken;
        this.userDto = userDto;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public UserDto getUserDto() {
        return userDto;
    }

    public void setUserDto(UserDto userDto) {
        this.userDto = userDto;
    }
}
