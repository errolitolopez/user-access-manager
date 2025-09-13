package com.example.demo.mail.mapper;

import com.example.demo.mail.dto.CreateSmtpConfigDto;
import com.example.demo.mail.dto.SmtpConfigDto;
import com.example.demo.mail.entity.SmtpConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper interface for converting between SmtpConfig entity and DTO.
 * This class uses MapStruct to provide efficient and clean mapping.
 */
@Mapper(componentModel = "spring")
public interface SmtpConfigMapper {

    /**
     * Converts an SmtpConfig entity to an SmtpConfigDto.
     *
     * @param smtpConfig The SmtpConfig entity.
     * @return The corresponding SmtpConfigDto.
     */
    SmtpConfigDto toDto(SmtpConfig smtpConfig);

    /**
     * Converts a list of SmtpConfig entities to a list of DTOs.
     *
     * @param smtpConfigs The list of SmtpConfig entities.
     * @return A list of SmtpConfigDto.
     */
    List<SmtpConfigDto> toDtoList(List<SmtpConfig> smtpConfigs);

    /**
     * Converts an SmtpConfigDto to an SmtpConfig entity.
     *
     * @param smtpConfigDto The SmtpConfigDto.
     * @return The corresponding SmtpConfig entity.
     */
    SmtpConfig toEntity(SmtpConfigDto smtpConfigDto);

    /**
     * Converts a CreateSmtpConfigDto to an SmtpConfig entity.
     *
     * @param createSmtpConfigDto The CreateSmtpConfigDto.
     * @return The corresponding SmtpConfig entity.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentSentCount", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "dateUpdated", ignore = true)
    SmtpConfig toEntity(CreateSmtpConfigDto createSmtpConfigDto);
}
