package com.example.demo.permission.mapper;

import com.example.demo.permission.dto.PermissionDto;
import com.example.demo.permission.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between Permission entity and Permission DTO.
 * This class uses MapStruct to provide efficient and clean mapping.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    /**
     * Converts a Permission entity to a PermissionDto.
     *
     * @param permission The Permission entity.
     * @return The corresponding PermissionDto.
     */
    PermissionDto toDto(Permission permission);

    /**
     * Converts a list of Permission entities to a list of PermissionDto.
     *
     * @param permissions The list of Permission entities.
     * @return A list of PermissionDto.
     */
    List<PermissionDto> toDtoList(List<Permission> permissions);

    /**
     * Converts a PermissionDto to a Permission entity.
     *
     * @param permissionDto The PermissionDto.
     * @return The corresponding Permission entity.
     */
    Permission toEntity(PermissionDto permissionDto);
}
