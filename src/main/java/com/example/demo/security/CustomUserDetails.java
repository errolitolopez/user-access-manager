package com.example.demo.security;

import com.example.demo.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A custom implementation of the UserDetails interface.
 * It wraps the User entity to provide Spring Security with the necessary information for authentication and authorization.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Retrieves the authorities (permissions) granted to the user.
     *
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            // Add the role itself (e.g., ROLE_ADMIN)
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            // Add the permissions associated with the role
            role.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getName()))
            );
        });
        return authorities;
    }

    /**
     * Retrieves the password used to authenticate the user.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Retrieves the username used to authenticate the user.
     *
     * @return The user's username.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * Indicates whether the user's account has expired.
     *
     * @return {@code true} if the user's account is valid (non-expired), {@code false} otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return !user.getAccountExpired();
    }

    /**
     * Indicates whether the user is locked or unlocked.
     *
     * @return {@code true} if the user is not locked, {@code false} otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return !user.getAccountLocked();
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     *
     * @return {@code true} if the user's credentials are valid, {@code false} otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return !user.getCredentialsExpired();
    }

    /**
     * Indicates whether the user is enabled or disabled.
     *
     * @return {@code true} if the user is enabled, {@code false} otherwise.
     */
    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
