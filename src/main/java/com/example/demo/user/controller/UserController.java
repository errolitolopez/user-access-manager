package com.example.demo.user.controller;

import com.example.demo.controller.BaseController;
import com.example.demo.security.dto.ApiResponse;
import com.example.demo.user.dto.CreateUserDto;
import com.example.demo.user.dto.UpdateUserEmailDto;
import com.example.demo.user.dto.UpdateUserPasswordDto;
import com.example.demo.user.dto.UserDto;
import com.example.demo.user.service.UserService;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Users.
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- Core CRUD Operations ---

    /**
     * Assigns a Role to a User.
     *
     * @param userId The ID of the User.
     * @param roleId The ID of the Role to assign.
     * @return A response entity indicating success.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    @PostMapping("/assign-role")
    public ResponseEntity<ApiResponse<Void>> addRoleToUser(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long userId,
                                                           @RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long roleId) {
        userService.addRoleToUser(userId, roleId);
        return buildSuccessResponse(HttpStatus.OK, "Role assigned to user successfully.");
    }

    /**
     * Creates a new User.
     *
     * @param createUserDto The details for the new User.
     * @return The created UserDto.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_USERS')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDto>> create(@RequestBody @Valid CreateUserDto createUserDto) {
        UserDto createdUser = userService.create(createUserDto);
        return buildSuccessResponse(HttpStatus.CREATED, "User created successfully.", createdUser);
    }

    /**
     * Deletes a User by ID.
     *
     * @param id The ID of the User to delete.
     * @return A response entity indicating success.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_USERS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long id) {
        userService.delete(id);
        return buildSuccessResponse(HttpStatus.NO_CONTENT, "User deleted successfully.");
    }

    /**
     * Retrieves a paginated list of all Users based on an example DTO.
     *
     * @param userDto  The UserDto to use as an example for the search.
     * @param pageable Pagination and sorting information.
     * @return A page of UserDtos.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_USERS')")
    public ResponseEntity<ApiResponse<Page<UserDto>>> getAll(UserDto userDto, Pageable pageable) {
        Page<UserDto> users = userService.getAll(userDto, pageable);
        return buildSuccessResponse(HttpStatus.OK, "Users retrieved successfully.", users);
    }

    /**
     * Retrieves a User by ID.
     *
     * @param id The ID of the User to retrieve.
     * @return The found UserDto.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_USERS')")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<UserDto>> getById(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long id) {
        UserDto userDto = userService.getById(id);
        return buildSuccessResponse(HttpStatus.OK, "User retrieved successfully.", userDto);
    }

    /**
     * Removes a Role from a User.
     *
     * @param userId The ID of the User.
     * @param roleId The ID of the Role to remove.
     * @return A response entity indicating success.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    @DeleteMapping("/remove-role")
    public ResponseEntity<ApiResponse<Void>> removeRoleFromUser(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long userId,
                                                                @RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long roleId) {
        userService.removeRoleFromUser(userId, roleId);
        return buildSuccessResponse(HttpStatus.OK, "Role removed from user successfully.");
    }

    // --- Role and Permission Management ---

    /**
     * Updates a User's email.
     *
     * @param updateUserEmailDto The details for the email update.
     * @return A response entity indicating success.
     */
    @PutMapping("/update/email")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    public ResponseEntity<ApiResponse<Void>> updateUserEmail(@Valid @RequestBody UpdateUserEmailDto updateUserEmailDto) {
        userService.updateUsersEmail(updateUserEmailDto);
        return buildSuccessResponse(HttpStatus.OK, "User email updated successfully.");
    }

    /**
     * Updates a User's password.
     *
     * @param updateUsersPassword The details for the password update.
     * @return A response entity indicating success.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    @PutMapping("/update/password")
    public ResponseEntity<ApiResponse<Void>> updateUserPassword(@Valid @RequestBody UpdateUserPasswordDto updateUsersPassword) {
        userService.updateUsersPassword(updateUsersPassword);
        return buildSuccessResponse(HttpStatus.OK, "User password updated successfully.");
    }

    // --- Account Status and Maintenance ---

    /**
     * Waives a user's account expiration status and extends the expiration date.
     *
     * @param userId The ID of the user whose expiration is to be waived.
     * @return A response entity indicating success.
     */
    @PutMapping("/waive-account-expiration")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    public ResponseEntity<ApiResponse<Void>> waiveAccountExpiration(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long userId) {
        userService.waiveAccountExpiration(userId);
        return buildSuccessResponse(HttpStatus.OK, "Account expiration waived successfully.");
    }

    /**
     * Waives the locked, expired credentials, and disabled status for a user.
     *
     * @param userId The ID of the user whose status is to be waived.
     * @return A response entity indicating success.
     */
    @PutMapping("/waive-status")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_USERS')")
    public ResponseEntity<ApiResponse<Void>> waiveUserStatus(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long userId) {
        userService.waiveUserStatus(userId);
        return buildSuccessResponse(HttpStatus.OK, "User status waived successfully.");
    }
}
