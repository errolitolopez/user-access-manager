package com.example.demo.security.service;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service for handling all JWT-related operations.
 */
@Service
public class JwtService {
    private final ApplicationConfigService applicationConfigService;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration:86400000}")
    private long jwtExpiration;

    public JwtService(ApplicationConfigService applicationConfigService) {
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * Extracts the username from a JWT.
     *
     * @param token The JWT string.
     * @return The username from the token's claims.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function to resolve the desired claim.
     * @param <T>            The type of the claim.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT for the given user.
     *
     * @param userDetails The UserDetails object.
     * @return The generated JWT string.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT with additional claims.
     *
     * @param extraClaims A map of additional claims.
     * @param userDetails The UserDetails object.
     * @return The generated JWT string.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long finalJwtExpiration = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.JWT_EXPIRATION))
                .map(Long::parseLong)
                .orElse(this.jwtExpiration);

        return Jwts.builder()
                .claims()
                .add(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + finalJwtExpiration))
                .and()
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validates a JWT against the user's details.
     *
     * @param token       The JWT to validate.
     * @param userDetails The UserDetails object.
     * @return {@code true} if the token is valid, {@code false} otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Extracts username from the JWT in the request.
     *
     * @param request The HTTP request.
     * @return An Optional containing the username if the token is valid, or empty otherwise.
     */
    public Optional<String> extractUsernameFromToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String jwt = authHeader.substring(7);
        try {
            return Optional.ofNullable(extractUsername(jwt));
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts all claims from a JWT.
     *
     * @param token The JWT string.
     * @return The Claims object from the token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if a JWT token has expired.
     *
     * @param token The JWT string.
     * @return {@code true} if the token has expired, {@code false} otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT.
     *
     * @param token The JWT string.
     * @return The expiration date.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Retrieves the signing key for the JWT.
     *
     * @return The signing key.
     */
    private SecretKey getSigningKey() {
        String finalSecretKey = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.JWT_SECRET_KEY))
                .orElse(this.secretKey);

        byte[] keyBytes = Decoders.BASE64.decode(finalSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
