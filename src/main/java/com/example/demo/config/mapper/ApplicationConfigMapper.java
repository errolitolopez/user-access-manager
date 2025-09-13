package com.example.demo.config.mapper;

import com.example.demo.config.dto.ApplicationConfigDto;
import com.example.demo.config.entity.ApplicationConfig;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting between ApplicationConfig entity and DTO.
 * This class uses MapStruct to provide efficient and clean mapping.
 */
@Mapper(componentModel = "spring")
public interface ApplicationConfigMapper {

    /**
     * Converts an ApplicationConfig entity to an ApplicationConfigDto.
     *
     * @param config The ApplicationConfig entity.
     * @return The corresponding ApplicationConfigDto.
     */
    ApplicationConfigDto toDto(ApplicationConfig config);

    /**
     * Converts a list of ApplicationConfig entities to a list of DTOs.
     *
     * @param configs The list of ApplicationConfig entities.
     * @return A list of ApplicationConfigDto.
     */
    List<ApplicationConfigDto> toDtoList(List<ApplicationConfig> configs);

    /**
     * Converts an ApplicationConfigDto to an ApplicationConfig entity.
     *
     * @param configDto The ApplicationConfigDto.
     * @return The corresponding ApplicationConfig entity.
     */
    ApplicationConfig toEntity(ApplicationConfigDto configDto);
}
