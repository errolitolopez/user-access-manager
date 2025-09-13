package com.example.demo.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service to identify a user for rate-limiting and auditing purposes.
 * It prioritizes the authenticated username, falling back to the IP address for unauthenticated requests.
 */
@Service
public class IdentityService {

    /**
     * Retrieves the username from the security context.
     *
     * @return The username if authenticated, otherwise null.
     */
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && isAnonymous(authentication)) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * Checks if the user is an anonymous user, which is the default for unauthenticated requests.
     *
     * @param authentication The Spring Security Authentication object.
     * @return {@code true} if the user is anonymous, {@code false} otherwise.
     */
    private boolean isAnonymous(Authentication authentication) {
        return !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Retrieves the client's IP address from the request headers.
     * This handles common proxy headers to get the correct IP.
     *
     * @param request The current HTTP request.
     * @return The client's IP address.
     */
    public String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "0.0.0.0";
        }
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
