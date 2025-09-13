package com.example.demo.permission.controller;

import com.example.demo.controller.BaseController;
import com.example.demo.permission.dto.PermissionDto;
import com.example.demo.permission.service.PermissionService;
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
 * REST controller for managing Permissions.
 */
@RestController
@RequestMapping("/api/permissions")
@Validated
public class PermissionController extends BaseController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Creates a new Permission.
     *
     * @param name The name for the new Permission.
     * @return The created PermissionDto.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('CREATE_PERMISSIONS')")
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PermissionDto>> create(@RequestParam @NotBlank(message = ValidationMessages.ID_CANNOT_BE_NULL) String name) {
        PermissionDto createdPermission = permissionService.create(name);
        return buildSuccessResponse(HttpStatus.CREATED, "Permission created successfully.", createdPermission);
    }

    /**
     * Deletes a Permission by ID.
     *
     * @param id The ID of the Permission to delete.
     * @return A response entity indicating success.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_PERMISSIONS')")
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long id) {
        permissionService.delete(id);
        return buildSuccessResponse(HttpStatus.NO_CONTENT, "Permission deleted successfully.");
    }

    /**
     * Retrieves a Permission by ID.
     *
     * @param id The ID of the Permission to retrieve.
     * @return The found PermissionDto.
     */
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_PERMISSIONS')")
    @GetMapping("/get")
    public ResponseEntity<ApiResponse<PermissionDto>> getById(@RequestParam @NotNull(message = ValidationMessages.ID_CANNOT_BE_NULL) Long id) {
        PermissionDto permissionDto = permissionService.getById(id);
        return buildSuccessResponse(HttpStatus.OK, "Permission retrieved successfully.", permissionDto);
    }

    /**
     * Retrieves a paginated list of all Permissions.
     *
     * @param permissionDto The PermissionDto with fields to match.
     * @param pageable      Pagination and sorting information.
     * @return A page of PermissionDtos.
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('READ_PERMISSIONS')")
    public ResponseEntity<ApiResponse<Page<PermissionDto>>> search(PermissionDto permissionDto, Pageable pageable) {
        Page<PermissionDto> permissions = permissionService.getAll(permissionDto, pageable);
        return buildSuccessResponse(HttpStatus.OK, "Permissions retrieved successfully.", permissions);
    }
}
