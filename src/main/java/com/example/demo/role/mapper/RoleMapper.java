package com.example.demo.role.mapper;

import com.example.demo.role.dto.RoleDto;
import com.example.demo.role.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between Role entity and Role DTO.
 * This class uses MapStruct to provide efficient and clean mapping.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Converts a Role entity to a RoleDto.
     *
     * @param role The Role entity.
     * @return The corresponding RoleDto.
     */
    RoleDto toDto(Role role);

    /**
     * Converts a list of Role entities to a list of RoleDto.
     *
     * @param roles The list of Role entities.
     * @return A list of RoleDto.
     */
    List<RoleDto> toDtoList(List<Role> roles);

    /**
     * Converts a RoleDto to a Role entity.
     *
     * @param roleDto The RoleDto.
     * @return The corresponding Role entity.
     */
    Role toEntity(RoleDto roleDto);
}
