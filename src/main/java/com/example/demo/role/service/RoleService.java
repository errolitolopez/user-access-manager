package com.example.demo.role.service;

import com.example.demo.role.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Assigns a Permission to a Role.
     *
     * @param permissionId The ID of the Permission to assign.
     * @param roleId       The ID of the Role.
     */
    void addPermissionToRole(Long permissionId, Long roleId);

    /**
     * Creates a new Role.
     *
     * @param name The name for the new Role.
     * @return The created RoleDto.
     */
    RoleDto create(String name);

    /**
     * Retrieves a paginated list of all Roles based on a query by example.
     *
     * @param roleDto  The RoleDto with fields to match.
     * @param pageable Pagination and sorting information.
     * @return A page of RoleDtos.
     */
    Page<RoleDto> getAll(RoleDto roleDto, Pageable pageable);

    /**
     * Retrieves a Role by ID.
     *
     * @param id The ID of the Role to retrieve.
     * @return The found RoleDto.
     */
    RoleDto getById(Long id);

    /**
     * Removes a Permission from a Role.
     *
     * @param permissionId The ID of the Permission to remove.
     * @param roleId       The ID of the Role.
     */
    void removePermissionFromRole(Long permissionId, Long roleId);
}
