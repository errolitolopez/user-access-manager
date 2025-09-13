package com.example.demo.user.service;

import com.example.demo.user.dto.CreateUserDto;
import com.example.demo.user.dto.UpdateUserEmailDto;
import com.example.demo.user.dto.UpdateUserPasswordDto;
import com.example.demo.user.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing users.
 */
public interface UserService {

    // --- Core CRUD Operations ---

    /**
     * Creates a new User.
     *
     * @param createUserDto DTO for creating a User.
     * @return The created UserDto.
     */
    UserDto create(CreateUserDto createUserDto);

    /**
     * Retrieves a User by ID.
     *
     * @param id The ID of the User to retrieve.
     * @return The found UserDto.
     */
    UserDto getById(Long id);

    /**
     * Retrieves a paginated list of all Users based on an example DTO.
     *
     * @param userDto The UserDto to use as an example for the search.
     * @param pageable Pagination and sorting information.
     * @return A page of UserDtos.
     */
    Page<UserDto> getAll(UserDto userDto, Pageable pageable);

    /**
     * Updates a User's email.
     *
     * @param updateUserEmailDto DTO for updating a User's email.
     */
    void updateUsersEmail(UpdateUserEmailDto updateUserEmailDto);

    /**
     * Updates a User's password.
     *
     * @param updateUserPasswordDto DTO for updating a User's password.
     */
    void updateUsersPassword(UpdateUserPasswordDto updateUserPasswordDto);

    /**
     * Deletes a User by ID.
     *
     * @param id The ID of the User to delete.
     */
    void delete(Long id);

    // --- Role and Permission Management ---

    /**
     * Assigns a Role to a User.
     *
     * @param userId The ID of the User.
     * @param roleId The ID of the Role.
     */
    void addRoleToUser(Long userId, Long roleId);

    /**
     * Removes a Role from a User.
     *
     * @param userId The ID of the User.
     * @param roleId The ID of the Role.
     */
    void removeRoleFromUser(Long userId, Long roleId);

    // --- Account Status and Maintenance ---

    /**
     * Waives the locked, expired credentials, and disabled status for a user.
     * This method resets the user's security-related flags.
     *
     * @param userId The ID of the User.
     */
    void waiveUserStatus(Long userId);

    /**
     * Waives the account expiration status and extends the account expiration date.
     *
     * @param userId The ID of the user whose account expiration status is to be waived.
     */
    void waiveAccountExpiration(Long userId);
}
