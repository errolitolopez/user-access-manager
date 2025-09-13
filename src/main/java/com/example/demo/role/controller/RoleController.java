package com.example.demo.role.controller;

import com.example.demo.controller.BaseController;
import com.example.demo.role.dto.RoleDto;
import com.example.demo.role.service.RoleService;
import com.example.demo.security.dto.ApiResponse;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing Roles.
 */
@RestController
@RequestMapping("/api/roles")
@Validated
public class RoleController extends BaseController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Assigns a Permission to a Role.
     *
     * @param permissionId The ID of the Permission to assign.
     * @param roleId       The ID of the Role.
     * @return A response entity with a success message.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_ROLES')")
    @PostMapping("/assign-permission")
    public ResponseEntity<ApiResponse<Void>> addPermissionToRole(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long permissionId,
                                                                 @RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long roleId) {
        roleService.addPermissionToRole(permissionId, roleId);
        return buildSuccessResponse(HttpStatus.OK, "Permission assigned to role successfully.");
    }

    /**
     * Creates a new Role.
     *
     * @param name The name for the new Role.
     * @return ResponseEntity with the created RoleDto.
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_ROLES')")
    public ResponseEntity<ApiResponse<RoleDto>> create(@RequestParam @NotBlank(message = ValidationMessages.NAME_CANNOT_BE_BLANK) String name) {
        RoleDto createdRole = roleService.create(name);
        return buildSuccessResponse(HttpStatus.CREATED, "Role created successfully.", createdRole);
    }

    /**
     * Retrieves a paginated list of all Roles.
     *
     * @param roleDto  The RoleDto with fields to match.
     * @param pageable Pagination and sorting information.
     * @return ResponseEntity with a page of RoleDtos.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_ROLES')")
    public ResponseEntity<ApiResponse<Page<RoleDto>>> search(RoleDto roleDto, Pageable pageable) {
        Page<RoleDto> roles = roleService.getAll(roleDto, pageable);
        return buildSuccessResponse(HttpStatus.OK, "Roles retrieved successfully.", roles);
    }

    /**
     * Retrieves a Role by its unique ID.
     *
     * @param id The ID of the Role to retrieve.
     * @return ResponseEntity with the found RoleDto.
     */
    @GetMapping("/get")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_ROLES')")
    public ResponseEntity<ApiResponse<RoleDto>> getById(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long id) {
        RoleDto role = roleService.getById(id);
        return buildSuccessResponse(HttpStatus.OK, "Role retrieved successfully.", role);
    }

    /**
     * Removes a Permission from a Role.
     *
     * @param permissionId The ID of the Permission to remove.
     * @param roleId       The ID of the Role.
     * @return A response entity with a success message.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_ROLES')")
    @DeleteMapping("/remove-permission")
    public ResponseEntity<ApiResponse<Void>> removePermissionFromRole(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long permissionId,
                                                                      @RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long roleId) {
        roleService.removePermissionFromRole(permissionId, roleId);
        return buildSuccessResponse(HttpStatus.NO_CONTENT, "Permission removed from role successfully.");
    }
}
