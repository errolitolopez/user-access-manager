package com.example.demo.security;

import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.security.service.CustomUserDetailsService;
import com.example.demo.util.UrlUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Spring Security configuration class.
 * This class configures the security filter chain, authentication provider, and access rules.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final RateLimitingFilter rateLimitingFilter;
    private final ApplicationConfigService applicationConfigService;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, CustomUserDetailsService userDetailsService, RateLimitingFilter rateLimitingFilter, ApplicationConfigService applicationConfigService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
        this.rateLimitingFilter = rateLimitingFilter;
        this.applicationConfigService = applicationConfigService;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] publicUrls = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.SECURITY_PUBLIC_URLS))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(UrlUtil::isValidUrlPath)
                        .map(url -> url.endsWith("/**") ? url : url + "/**")
                        .toArray(String[]::new))
                .orElse(new String[]{"/api/auth/**", "/api/public/password-reset/request"});

        String[] privateUrls = Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.SECURITY_PRIVATE_URLS))
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(UrlUtil::isValidUrlPath)
                        .map(url -> url.endsWith("/**") ? url : url + "/**")
                        .toArray(String[]::new))
                .orElse(new String[]{});

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicUrls).permitAll()
                        .requestMatchers(privateUrls).authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password hashing.
     *
     * @return An instance of PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes the AuthenticationManager bean.
     *
     * @return The AuthenticationManager.
     * @throws Exception If an error occurs.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
