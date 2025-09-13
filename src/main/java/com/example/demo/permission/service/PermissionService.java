package com.example.demo.permission.service;

import com.example.demo.permission.dto.PermissionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing Permissions.
 */
public interface PermissionService {

    /**
     * Creates a new Permission.
     *
     * @param name The name for the new Permission.
     * @return The created PermissionDto.
     */
    PermissionDto create(String name);

    /**
     * Deletes a Permission by ID.
     *
     * @param id The ID of the Permission to delete.
     */
    void delete(Long id);

    /**
     * Retrieves a paginated list of all Permissions based on a query by example.
     *
     * @param permissionDto The PermissionDto with fields to match.
     * @param pageable Pagination and sorting information.
     * @return A page of PermissionDtos.
     */
    Page<PermissionDto> getAll(PermissionDto permissionDto, Pageable pageable);

    /**
     * Retrieves a Permission by ID.
     *
     * @param id The ID of the Permission to retrieve.
     * @return The found PermissionDto.
     */
    PermissionDto getById(Long id);
}

