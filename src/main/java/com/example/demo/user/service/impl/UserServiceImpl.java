package com.example.demo.user.service.impl;

import com.example.demo.audit.service.AuditService;
import com.example.demo.config.constants.ApplicationConfigKeys;
import com.example.demo.config.service.ApplicationConfigService;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.role.entity.Role;
import com.example.demo.role.repository.RoleRepository;
import com.example.demo.security.service.IdentityService;
import com.example.demo.user.dto.CreateUserDto;
import com.example.demo.user.dto.UpdateUserEmailDto;
import com.example.demo.user.dto.UpdateUserPasswordDto;
import com.example.demo.user.dto.UserDto;
import com.example.demo.user.entity.User;
import com.example.demo.user.mapper.UserMapper;
import com.example.demo.user.repository.UserRepository;
import com.example.demo.user.service.UserService;
import com.example.demo.util.constants.ValidationMessages;
import com.example.demo.util.enums.EventType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final int DEFAULT_ACCOUNT_EXPIRATION_YEARS = 3;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;
    private final IdentityService identityService;
    private final ApplicationConfigService applicationConfigService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           UserMapper userMapper,
                           AuditService auditService,
                           IdentityService identityService,
                           ApplicationConfigService applicationConfigService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.auditService = auditService;
        this.identityService = identityService;
        this.applicationConfigService = applicationConfigService;
        this.passwordEncoder = passwordEncoder;
    }

    // --- Core CRUD Operations ---

    @Override
    @Transactional
    public UserDto create(CreateUserDto createUserDto) {
        validateUserUniqueness(createUserDto.getUsername(), createUserDto.getEmail());

        User user = userMapper.toEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setPasswordLastUpdated(LocalDateTime.now());
        user.setAccountExpirationDate(LocalDateTime.now().plusYears(getAccountExpirationYears()));
        user.setRoles(roleRepository.findByName("ROLE_USER").stream().collect(Collectors.toSet()));
        User savedUser = userRepository.save(user);

        // Audit the creation of the new user
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", savedUser.getId());
        details.put("username", savedUser.getUsername());
        auditService.logEvent(username, null, EventType.CREATE_USER, details);

        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAll(UserDto userDto, Pageable pageable) {
        User userExample = userMapper.toEntity(userDto);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("roles")
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<User> example = Example.of(userExample, matcher);

        return userRepository.findAll(example, pageable)
                .map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void updateUsersEmail(UpdateUserEmailDto updateUserEmailDto) {
        User user = getUserById(updateUserEmailDto.getId());

        validateEmailUpdate(user.getId(), user.getEmail(), updateUserEmailDto.getEmail());

        String oldEmail = user.getEmail();
        user.setEmail(updateUserEmailDto.getEmail());
        user.setDateUpdated(LocalDateTime.now());
        userRepository.save(user);

        // Audit the email update
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("oldEmail", oldEmail);
        details.put("newEmail", user.getEmail());
        auditService.logEvent(username, null, EventType.UPDATE_EMAIL, details);
    }

    @Override
    @Transactional
    public void updateUsersPassword(UpdateUserPasswordDto updateUserPasswordDto) {
        User user = getUserById(updateUserPasswordDto.getId());

        if (passwordEncoder.matches(updateUserPasswordDto.getPassword(), user.getPassword())) {
            throw new InvalidInputException("New password cannot be the same as the old password.");
        }
        user.setPassword(passwordEncoder.encode(updateUserPasswordDto.getPassword()));
        user.setPasswordLastUpdated(LocalDateTime.now());
        if (user.getCredentialsExpired()) {
            user.setCredentialsExpired(false);
        }
        userRepository.save(user);

        // Audit the password update
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        auditService.logEvent(username, null, EventType.UPDATE_PASSWORD, details);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User userToDelete = getUserById(id);
        userRepository.delete(userToDelete);

        // Audit the user deletion
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", userToDelete.getId());
        details.put("username", userToDelete.getUsername());
        auditService.logEvent(username, null, EventType.DELETE_USER, details);
    }

    // --- Role and Permission Management ---

    @Override
    @Transactional
    public void addRoleToUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        user.getRoles().add(role);
        userRepository.save(user);

        // Audit the role assignment
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        details.put("roleId", role.getId());
        details.put("roleName", role.getName());
        auditService.logEvent(username, null, EventType.ASSIGN_ROLE, details);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = getUserById(userId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));

        user.getRoles().remove(role);
        userRepository.save(user);

        // Audit the role removal
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        details.put("roleId", role.getId());
        details.put("roleName", role.getName());
        auditService.logEvent(username, null, EventType.REMOVE_ROLE, details);
    }

    // --- Account Status and Maintenance ---

    @Override
    @Transactional
    public void waiveUserStatus(Long userId) {
        User user = getUserById(userId);

        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setAccountExpired(false);
        user.setCredentialsExpired(false);
        user.setFailedLoginAttempts(0);
        user.setLastFailedLoginTime(null);
        user.setAccountExpirationDate(LocalDateTime.now().plusYears(getAccountExpirationYears()));
        userRepository.save(user);

        // Audit the status waiver
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        auditService.logEvent(username, null, EventType.WAIVE_STATUS, details);
    }

    @Override
    @Transactional
    public void waiveAccountExpiration(Long userId) {
        User user = getUserById(userId);

        user.setAccountExpired(false);
        user.setAccountExpirationDate(LocalDateTime.now().plusYears(getAccountExpirationYears()));
        userRepository.save(user);

        // Audit the account expiration waiver
        String username = identityService.getAuthenticatedUsername();
        Map<String, Object> details = new HashMap<>();
        details.put("userId", user.getId());
        details.put("username", user.getUsername());
        auditService.logEvent(username, null, EventType.WAIVE_ACCOUNT_EXPIRATION, details);
    }

    /**
     * Retrieves the account expiration years from the application configuration service, with a fallback.
     *
     * @return The configured expiration years or the default value.
     */
    private int getAccountExpirationYears() {
        return Optional.ofNullable(applicationConfigService.getValue(ApplicationConfigKeys.ACCOUNT_EXPIRATION_YEARS))
                .map(Integer::parseInt)
                .orElse(DEFAULT_ACCOUNT_EXPIRATION_YEARS);
    }

    /**
     * Validates that a new user's username and email are unique before creation.
     *
     * @param username The username to check for uniqueness.
     * @param email    The email to check for uniqueness.
     * @throws InvalidInputException if the username or email already exists.
     */
    private void validateUserUniqueness(String username, String email) {
        userRepository.findByUsernameOrEmail(username, email)
                .stream()
                .findFirst()
                .ifPresent(existingUser -> {
                    if (existingUser.getUsername().equalsIgnoreCase(username)) {
                        throw new InvalidInputException(ValidationMessages.USERNAME_ALREADY_EXISTS);
                    }
                    if (existingUser.getEmail().equalsIgnoreCase(email)) {
                        throw new InvalidInputException(ValidationMessages.EMAIL_ALREADY_EXISTS);
                    }
                });
    }

    /**
     * Helper method to find a user by ID or throw a ResourceNotFoundException.
     * This reduces code duplication for common find-by-ID operations.
     *
     * @param id The ID of the user to find.
     * @return The User entity.
     * @throws ResourceNotFoundException if the user is not found.
     */
    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * Helper method to validate the new email for an existing user.
     *
     * @param userId       The ID of the user being updated.
     * @param currentEmail The current email of the user.
     * @param newEmail     The new email to validate.
     * @throws InvalidInputException if the new email is the same as the old or is already in use by another user.
     */
    private void validateEmailUpdate(Long userId, String currentEmail, String newEmail) {
        if (currentEmail.equalsIgnoreCase(newEmail)) {
            throw new InvalidInputException("New email cannot be the same as the current email.");
        }

        userRepository.findByEmail(newEmail)
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(userId)) {
                        throw new InvalidInputException(ValidationMessages.EMAIL_ALREADY_EXISTS);
                    }
                });
    }
}
