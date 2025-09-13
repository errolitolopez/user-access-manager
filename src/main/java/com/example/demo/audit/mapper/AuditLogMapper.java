package com.example.demo.audit.mapper;

import com.example.demo.audit.dto.AuditLogDto;
import com.example.demo.audit.entity.AuditLog;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    /**
     * Converts an AuditLog entity to an AuditLogDto.
     *
     * @param auditLog The AuditLog entity.
     * @return The corresponding AuditLogDto.
     */
    AuditLogDto toDto(AuditLog auditLog);

    /**
     * Converts a list of AuditLog entities to a list of AuditLogDto.
     *
     * @param auditLogs The list of AuditLog entities.
     * @return A list of AuditLogDto.
     */
    List<AuditLogDto> toDtoList(List<AuditLog> auditLogs);

    /**
     * Converts an AuditLogDto to an AuditLog entity.
     *
     * @param auditLogDto The AuditLogDto.
     * @return The corresponding AuditLog entity.
     */
    AuditLog toEntity(AuditLogDto auditLogDto);
}
