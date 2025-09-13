package com.example.demo.security.controller;

import com.example.demo.controller.BaseController;
import com.example.demo.security.dto.ApiResponse;
import com.example.demo.security.dto.AuthenticationRequest;
import com.example.demo.security.dto.AuthenticationResponse;
import com.example.demo.security.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling authentication-related requests.
 * This controller provides endpoints for user login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController extends BaseController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     *
     * @param request The authentication request containing the username and password.
     * @return A ResponseEntity with the JWT token and user details in the body.
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest httpRequest) {
        AuthenticationResponse response = authenticationService.authenticate(request, httpRequest);
        return buildSuccessResponse(HttpStatus.OK, "Authentication successful.", response);
    }
}
