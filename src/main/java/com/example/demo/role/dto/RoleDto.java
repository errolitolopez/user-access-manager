package com.example.demo.role.dto;

import com.example.demo.permission.dto.PermissionDto;
import com.example.demo.util.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Set;

/**
 * Data Transfer Object for the Role entity.
 */
public class RoleDto {

    /**
     * The unique identifier for the role.
     */
    private Long id;

    /**
     * The name of the role.
     */
    @NotBlank(message = ValidationMessages.NAME_CANNOT_BE_BLANK)
    private String name;

    /**
     * A list of permissions associated with the role.
     */
    private Set<PermissionDto> permissions;

    public RoleDto() {
    }

    public RoleDto(Long id, String name, Set<PermissionDto> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionDto> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDto> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "RoleDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
