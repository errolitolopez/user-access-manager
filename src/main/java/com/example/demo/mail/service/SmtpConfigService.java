package com.example.demo.mail.service;

import com.example.demo.mail.dto.CreateSmtpConfigDto;
import com.example.demo.mail.dto.SmtpConfigDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing SMTP configurations.
 */
public interface SmtpConfigService {

    /**
     * Creates a new SMTP configuration.
     *
     * @param createSmtpConfigDto The DTO containing the SMTP configuration data.
     * @return The created SmtpConfigDto.
     */
    SmtpConfigDto create(CreateSmtpConfigDto createSmtpConfigDto);

    /**
     * Deletes an SMTP configuration by its ID.
     *
     * @param id The ID of the configuration to delete.
     */
    void delete(Long id);

    /**
     * Updates an existing SMTP configuration.
     *
     * @param smtpConfigDto The DTO with the updated SMTP configuration data.
     * @return The updated SmtpConfigDto.
     */
    SmtpConfigDto update(SmtpConfigDto smtpConfigDto);

    /**
     * Toggles the enabled status of an SMTP configuration.
     *
     * @param id The ID of the configuration to update.
     * @param enabled The new enabled status.
     */
    void updateEnabledStatus(Long id, Boolean enabled);

    /**
     * Retrieves a paginated list of all SMTP configurations based on a search example.
     *
     * @param smtpConfigDto The DTO to use as an example for the search.
     * @param pageable      Pagination and sorting information.
     * @return A page of SmtpConfigDto.
     */
    Page<SmtpConfigDto> getAll(SmtpConfigDto smtpConfigDto, Pageable pageable);

    /**
     * Retrieves an SMTP configuration by its ID.
     *
     * @param id The ID of the configuration to retrieve.
     * @return The found SmtpConfigDto.
     */
    SmtpConfigDto getById(Long id);

    /**
     * Retrieves an SMTP configuration by its unique name.
     *
     * @param name The name of the configuration to retrieve.
     * @return The found SmtpConfigDto.
     */
    SmtpConfigDto getByName(String name);

    /**
     * Retrieves all enabled SMTP configurations.
     *
     * @return A list of SmtpConfigDto for all enabled configurations.
     */
    List<SmtpConfigDto> getAllEnabledConfigs();

    /**
     * Increments the sent count for a specified SMTP configuration.
     * This method is intended to be called after a successful email send operation.
     * * @param configName The name of the SMTP configuration.
     */
    void incrementSentCount(String configName);
}
